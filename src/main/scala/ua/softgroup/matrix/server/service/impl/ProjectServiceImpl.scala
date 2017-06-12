package ua.softgroup.matrix.server.service.impl

import java.io.IOException
import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}
import java.util.{NoSuchElementException, Optional}
import java.util

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.api.model.datamodels.{ProjectModel, TimeModel}
import ua.softgroup.matrix.server.persistent.entity.{Project, User, WorkDay, WorkTimePeriod}
import ua.softgroup.matrix.server.persistent.repository.ProjectRepository
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService, WorkTimePeriodService}
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint
import ua.softgroup.matrix.server.supervisor.consumer.json.ProjectJson
import ua.softgroup.matrix.server.supervisor.producer.Utils.calculateIdlePercent

import scala.collection.JavaConverters._

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Service
class ProjectServiceImpl @Autowired() (
                         repository: ProjectRepository,
                         supervisorEndpoint: SupervisorEndpoint,
                         userService: UserService,
                         workDayService: WorkDayService,
                         workTimePeriodService: WorkTimePeriodService) extends ProjectService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private var currencyMap: Map[Int, String] = Map()

  override def saveStartWorkTime(userToken: String, projectId: Long): TimeModel = {
    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = getById(projectId).orElseThrow(() => new NoSuchElementException)

    logger.info(s"Start work of user ${user.getUsername} on project $projectId at ${LocalDateTime.now}")

    project.setWorkStarted(LocalDateTime.now)
    project.setCheckpointTime(null)
    project.setEndDate(null)
    repository.save(project)

    val workDay = workDayService.save(workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now)
                                                    .orElseGet(() => new WorkDay(user, project, LocalDate.now)))

    workTimePeriodService.save(new WorkTimePeriod(project.getWorkStarted.toLocalTime, LocalTime.now, workDay))

    val arrivalTime = if (workDayService.getStartWorkOf(workDay) == null) project.getWorkStarted.toLocalTime
                      else workDayService.getStartWorkOf(workDay)

    new TimeModel(0, 0, arrivalTime, 0)
  }

  override def saveEndWorkTime(userToken: String, projectId: Long): TimeModel = {
    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = getById(projectId).orElseThrow(() => new NoSuchElementException)

    logger.info(s"End work of user ${user.getUsername} on project $projectId at ${LocalDateTime.now}")

    val startedWork = Optional.ofNullable(project.getCheckpointTime).orElse(project.getWorkStarted)

    val seconds = (Duration.between(startedWork, LocalDateTime.now).toMillis / 1000).toInt
    logger.debug(s"User ${user.getUsername} worked $seconds seconds on project ${project.getId}")

    val workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now)
                                .orElseGet(() => new WorkDay(user, project, LocalDate.now))
    workDay.setWorkSeconds(workDay.getWorkSeconds + seconds)
    workDay.setRate(project.getRate)
    workDay.setCurrencyId(project.getRateCurrencyId)
    workDayService.save(workDay)

    val workTimePeriod = workTimePeriodService.getLatestPeriodOf(workDay).orElseThrow(() => new NoSuchElementException)
    workTimePeriod.setEnd(LocalTime.now)
    workTimePeriodService.save(workTimePeriod)

    project.setWorkStarted(null)
    project.setCheckpointTime(null)
    save(project)

    val totalWorkSeconds = workDayService.getTotalWorkSeconds(user, project)
    val downtimePercent = calculateIdlePercent(workDay.getWorkSeconds, workDay.getIdleSeconds)
    new TimeModel(totalWorkSeconds, workDay.getWorkSeconds, downtimePercent)
  }

  override def saveCheckpointTime(userToken: String, projectId: Long, idleTime: Int): TimeModel = {
    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = getById(projectId).orElseThrow(() => new NoSuchElementException)

    logger.info(s"Checkpoint of user ${user.getUsername} on project $projectId at ${LocalDateTime.now}")

    val now = LocalDateTime.now
    val previousCheckpoint = Optional.ofNullable(project.getCheckpointTime).orElse(project.getWorkStarted)
    val seconds = (Duration.between(previousCheckpoint, now).toMillis / 1000).toInt
    logger.debug(s"User ${user.getUsername} worked $seconds and idle $idleTime seconds on project ${project.getId}")

    project.setCheckpointTime(now)
    save(project)

    val workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now)
                                .orElseGet(() => new WorkDay(user, project, LocalDate.now))
    workDay.setWorkSeconds(workDay.getWorkSeconds + seconds)
    workDay.setIdleSeconds(workDay.getIdleSeconds + idleTime)
    workDay.setRate(project.getRate)
    workDay.setCurrencyId(project.getRateCurrencyId)
    workDayService.save(workDay)

    val workTimePeriod = workTimePeriodService.getLatestPeriodOf(workDay).orElseThrow(() => new NoSuchElementException)
    workTimePeriod.setEnd(LocalTime.now)
    workTimePeriodService.save(workTimePeriod)

    val totalWorkSeconds = workDayService.getTotalWorkSeconds(user, project)
    val downtimePercent = calculateIdlePercent(workDay.getWorkSeconds, workDay.getIdleSeconds)
    val arrivalTime = if (workDayService.getStartWorkOf(workDay) == null) project.getWorkStarted.toLocalTime
                      else workDayService.getStartWorkOf(workDay)

    new TimeModel(totalWorkSeconds, workDay.getWorkSeconds, arrivalTime, downtimePercent)
  }

  override def getUserActiveProjects(token: String): util.Set[ProjectModel] = {
    val user = userService.getByTrackerToken(token).orElseThrow(() => new NoSuchElementException)
    var projectStream: Seq[Project] = null

    logger.info(s"User '${user.username}' requesting his active projects")

    try {
      if (currencyMap.isEmpty) queryCurrencies(token)
      projectStream = queryUserActiveProjects(token).getList
        .map(project => addUserAndSaveProject(project, user))
    } catch {
      case e: Exception =>
        logger.warn("Failed to fetch user's active projects from Supervisor:", e)
        projectStream = asScalaBuffer(repository.findByUser(user))
    }

    new util.HashSet(setAsJavaSet(
      projectStream
        .filter(project => project.getEndDate == null || LocalDate.now.isBefore(project.getEndDate)) //TODO maybe change this
        .map(convertProjectEntityToModel)
        .toSet
    ))
  }

  override def getUserActiveProjects(userId: Long): Set[Project] =
    asScalaSet(repository.findByUserId(userId)).toSet

  override def getBySupervisorIdAndUser(supervisorId: Long, user: User): Optional[Project] =
    Optional.ofNullable(repository.findBySupervisorIdAndUser(supervisorId, user))

  override def getBySupervisorId(supervisorId: Long): Set[Project] =
    asScalaSet(repository.findBySupervisorId(supervisorId)).toSet

  override def getById(id: Long): Optional[Project] = Optional.ofNullable(repository.findOne(id))

  override def save(entity: Project): Project = repository.save(entity)

  override def isExist(id: Long): Boolean = repository.exists(id)

  @throws[IOException]
  private def queryCurrencies(token: String) = {
    val response = supervisorEndpoint
      .getCurrencies(token)
      .execute

    if (!response.isSuccessful) throw new IOException(s"Failed to query get-currencies. ${response.errorBody.string}")

    currencyMap = asScalaBuffer(response.body.getList)
      .map(currencyJson => (currencyJson.getId, currencyJson.getName))
      .toMap
  }

  @throws[IOException]
  private def queryUserActiveProjects(token: String) = {
    val response = supervisorEndpoint
      .getUserActiveProjects(token)
      .execute

    if (!response.isSuccessful) throw new IOException(s"Failed to query get-user-active-projects. ${response.errorBody.string}")

    response.body
  }

  private def addUserAndSaveProject(projectJson: ProjectJson, user: User) = {
    val project = getBySupervisorIdAndUser(projectJson.getId, user).orElseGet(() => new Project)
    project.setSupervisorId(projectJson.getId)
    project.setAuthorName(projectJson.getAuthorName)
    project.setDescription(projectJson.getDescription)
    project.setTitle(projectJson.getTitle)
    project.setStartDate(projectJson.getStartDate)
    project.setEndDate(projectJson.getEndDate)
    project.setRate(projectJson.getRate)
    project.setRateCurrencyId(projectJson.getRateCurrencyId)
    project.setUser(user)

    repository.save(project)
  }

  private def convertProjectEntityToModel(project: Project) = {
    val projectModel = new ProjectModel
    projectModel.setId(project.getId)
    projectModel.setTitle(project.getTitle)
    projectModel.setDescription(project.getDescription)
    projectModel.setAuthorName(project.getAuthorName)
    projectModel.setStartDate(project.getStartDate)
    projectModel.setEndDate(project.getEndDate)
    projectModel.setRate(project.getRate)
    projectModel.setRateCurrency(currencyMap.getOrElse(project.getRateCurrencyId, "?"))

    val user = project.getUser
    val totalWorkSeconds = workDayService.getTotalWorkSeconds(user, project)
    val currentMonthIdleSeconds = workDayService.getCurrentMonthIdleSeconds(user, project)
    val downtimePercent = calculateIdlePercent(totalWorkSeconds, currentMonthIdleSeconds)
    val workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now).orElseGet(() => new WorkDay)
    val arrivalTime = workDayService.getStartWorkOf(if (workDay.isNew) null else workDay)

    projectModel.setProjectTime(new TimeModel(totalWorkSeconds, workDay.getWorkSeconds, arrivalTime, downtimePercent))

    projectModel
  }

}
