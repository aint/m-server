package ua.softgroup.matrix.server.service.impl

import java.time.{LocalDate, LocalTime}
import java.time.temporal.TemporalAdjusters.{firstDayOfMonth, lastDayOfMonth}
import java.util
import java.util.{NoSuchElementException, Optional}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.api.model.datamodels.ReportModel
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus
import ua.softgroup.matrix.server.persistent.entity.{Project, User, WorkDay, WorkTimePeriod}
import ua.softgroup.matrix.server.persistent.repository.{ProjectRepository, WorkDayRepository, WorkTimePeriodRepository}
import ua.softgroup.matrix.server.service.{UserService, WorkDayService}

import scala.collection.JavaConverters._

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Service
class WorkDayServiceImpl @Autowired() (repository: WorkDayRepository,
                                       projectRepository: ProjectRepository,
                                       workTimePeriodRepository: WorkTimePeriodRepository,
                                       userService: UserService) extends WorkDayService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val UAH_CODE = "UAH"
  private val USD_CODE = "USD"

  override def getTotalWorkSeconds(author: User, project: Project): Int =
    Optional.ofNullable(repository.getTotalWorkSeconds(author.getId, project.getId)).orElse(0)

  override def getTotalWorkSeconds(author: User, date: LocalDate): Int =
    Optional.ofNullable(repository.getTotalWorkSeconds(author.getId, date)).orElse(0)

  override def getCurrentMonthIdleSeconds(author: User, project: Project): Int = {
    val start = LocalDate.now.`with`(firstDayOfMonth)
    val end = LocalDate.now.`with`(lastDayOfMonth)
    Optional.ofNullable(repository.getCurrentMonthIdleSeconds(author.getId, project.getId, start, end)).orElse(0)
  }

  override def getTotalIdleSeconds(author: User, project: Project): Int =
    Optional.ofNullable(repository.getTotalIdleSeconds(author.getId, project.getId)).orElse(0)

  override def getTotalIdleSeconds(author: User, date: LocalDate): Int =
    Optional.ofNullable(repository.getTotalIdleSeconds(author.getId, date)).orElse(0)

  override def getTotalWorkSeconds(userId: Long, from: LocalDate, to: LocalDate): Int =
    Optional.ofNullable(repository.getTotalWorkSeconds(userId, from, to)).orElse(0)

  override def getTotalIdleSeconds(userId: Long, from: LocalDate, to: LocalDate): Int =
    Optional.ofNullable(repository.getTotalIdleSeconds(userId, from, to)).orElse(0)

  override def getSymbolsCount(userId: Long, from: LocalDate, to: LocalDate): Int =
    Optional.ofNullable(repository.getSymbolsCount(userId, from, to)).orElse(0)

  override def getWindowsSwitchedCount(userId: Long, from: LocalDate, to: LocalDate): Int =
    Optional.ofNullable(repository.getWindowsSwitchedCount(userId, from, to)).orElse(0)

  override def getByAuthorAndProjectAndDate(author: User, project: Project, localDate: LocalDate): Optional[WorkDay] =
    Optional.ofNullable(repository.findByAuthorAndProjectAndDate(author, project, localDate))

  override def getAllWorkDaysOf(userId: Long, projectSupervisorId: Long, from: LocalDate, to: LocalDate): Set[WorkDay] =
    asScalaSet(repository.findByAuthorIdAndProjectSupervisorIdAndDateBetween(userId, projectSupervisorId, from, to)).toSet

  override def getAllWorkDaysOf(user: User, localDate: LocalDate): util.Set[WorkDay] =
    repository.findByAuthorAndDate(user, localDate)

  override def getAllWorkDaysOf(projectSupervisorId: Long, date: LocalDate): util.Set[WorkDay] =
    repository.findByProjectSupervisorIdAndDate(projectSupervisorId, date)

  override def getUserWorkDaysBetween(userId: Long, from: LocalDate, to: LocalDate): Set[WorkDay] =
    asScalaSet(repository.findByAuthorIdAndDateBetween(userId, from, to)).toSet

  override def getUserNotCheckedWorkDays(userId: Long): Set[WorkDay] =
    asScalaSet(repository.findByAuthorIdAndCheckedFalse(userId)).toSet

  override def getProjectNotCheckedWorkDays(projectSupervisorId: Long): Set[WorkDay] =
    asScalaSet(repository.findByProjectSupervisorIdAndCheckedFalse(projectSupervisorId)).toSet

  override def getAllNotCheckedWorkDays: util.Set[WorkDay] = repository.findByCheckedFalse

  override def getWorkDaysBetween(from: LocalDate, to: LocalDate): Set[WorkDay] =
    asScalaSet(repository.findByDateBetween(from, to)).toSet

  override def getProjectWorkDaysBetween(projectSupervisorId: Long, from: LocalDate, to: LocalDate): Set[WorkDay] =
    asScalaSet(repository.findByProjectSupervisorIdAndDateBetween(projectSupervisorId, from, to)).toSet

  override def getWorkDaysOf(userToken: String, projectId: Long): util.Set[ReportModel] = {
    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = Optional.ofNullable(projectRepository.findOne(projectId)).orElseThrow(() => new NoSuchElementException)

    logger.info(s"Request user's '${user.getUsername}' reports of project $projectId")

    val workDays = asScalaSet(repository.findByAuthorAndProject(user, project))
      .map(convertEntityToDto)
      .toSet

    new util.HashSet(setAsJavaSet(workDays))
  }

  //TODO maybe throw exception instead of return status?
  def saveReportOrUpdate(userToken: String, projectId: Long, reportModel: ReportModel): ResponseStatus = {
    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = Optional.ofNullable(projectRepository.findOne(projectId)).orElseThrow(() => new NoSuchElementException)

    val workDay = if (reportModel.getId != 0L) repository.findOne(reportModel.getId)
                  else Optional.ofNullable(repository.findByAuthorAndProjectAndDate(user, project, reportModel.getDate))
                               .orElseThrow(() => new NoSuchElementException) //TODO use id for repo, not objects

    if (workDay.isChecked) {
      logger.warn(s"Report ${workDay.getId} of user '${user.getUsername}' is checked")
      return ResponseStatus.REPORT_EXPIRED
    }

    logger.info(s"Save/update user '${user.getUsername}' report of project '${project.getId}'")

    workDay.setReportText(reportModel.getText)
    repository.save(workDay)

    ResponseStatus.SUCCESS
  }

  override def getStartWorkOf(workDay: WorkDay): LocalTime =
    Optional.ofNullable(workTimePeriodRepository.findTopByWorkDayOrderByStartAsc(workDay))
            .orElseGet(() => new WorkTimePeriod)
      .getStart

  override def getEndWorkOf(workDay: WorkDay): LocalTime =
    Optional.ofNullable(workTimePeriodRepository.findTopByWorkDayOrderByEndDesc(workDay))
            .orElseGet(() => new WorkTimePeriod)
      .getEnd


  override def getById(id: Long): Optional[WorkDay] = Optional.ofNullable(repository.findOne(id))

  override def save(entity: WorkDay): WorkDay = repository.save(entity)

  override def isExist(id: Long): Boolean = repository.exists(id)

  private def convertEntityToDto(workDay: WorkDay) = {
    val reportModel = new ReportModel
    reportModel.setId(workDay.getId)
    reportModel.setRate(workDay.getProject.getRate)
    reportModel.setCurrency(if (workDay.getProject.getRateCurrencyId == 1) USD_CODE else UAH_CODE)
    reportModel.setCoefficient(workDay.getCoefficient)
    reportModel.setText(workDay.getReportText)
    reportModel.setDate(workDay.getDate)
    reportModel.setChecked(workDay.isChecked)
    reportModel.setWorkTime(workDay.getWorkSeconds)

    reportModel
  }

}
