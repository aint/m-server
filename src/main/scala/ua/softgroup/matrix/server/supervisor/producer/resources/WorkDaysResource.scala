package ua.softgroup.matrix.server.supervisor.producer.resources

import java.time.{LocalDate, LocalTime}
import java.time.temporal.ChronoUnit
import javax.validation.constraints.Min
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import org.hibernate.validator.constraints.NotBlank
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ua.softgroup.matrix.server.persistent.entity.{User, WorkDay}
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService}
import ua.softgroup.matrix.server.supervisor.producer.Utils.{calculateIdlePercent, parseData, validateEndRangeDate}
import ua.softgroup.matrix.server.supervisor.producer.json.v2._

import scala.collection.JavaConverters.asScalaSet

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Component
@Path("/workdays")
@Api("/workdays")
class WorkDaysResource @Autowired()(projectService: ProjectService,
                                    userService: UserService,
                                    workDayService: WorkDayService) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @GET
  @Path("/users/{userId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "1) getUserWorkingDays", response = classOf[UserWorkingDay], responseContainer = "List")
  @Transactional
  def getUserWorkingDays(@ApiParam(example = "14")         @PathParam("userId")    @Min(1)   userId: Long,
                         @ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotBlank fromDate: String,
                         @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotBlank toDate: String): Response = {

    logger.info(s"Get working day of user $userId from $fromDate to $toDate")

    val user: User = userService.getById(userId).orElseGet(() => new User)

    val from: LocalDate = parseData(fromDate)
    val to: LocalDate = validateEndRangeDate(parseData(toDate))

    val result = (0 until ChronoUnit.DAYS.between(from, to).toInt).toStream
      .map(index => workDayService.getAllWorkDaysOf(user, from.plusDays(index)))
      .filterNot(_.isEmpty)
      .map(s => converterToUserWorkingDay(asScalaSet(s).toSet))
      .toList

    Response.ok(result).build
  }

  @GET
  @Path("/project/{entityId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "2) getEntityWorkingDays", response = classOf[ProjectWorkingDay], responseContainer = "List")
  @Transactional
  def getEntityWorkingDays(@ApiParam(example = "14")         @PathParam("entityId")  @Min(1)   projectId: Long,
                           @ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotBlank fromDate: String,
                           @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotBlank toDate: String): Response = {

    logger.info(s"Get working day of project $projectId from $fromDate to $toDate")

    val from = parseData(fromDate)
    val to = validateEndRangeDate(parseData(toDate))

    val result = (0 until ChronoUnit.DAYS.between(from, to).toInt).toStream
      .map(index => workDayService.getAllWorkDaysOf(projectId, from.plusDays(index)))
      .filterNot(_.isEmpty)
      .map(s => convertToProjectWorkingDay(asScalaSet(s).toSet))
      .toList

    Response.ok(result).build
  }

  def convertToProjectWorkingDay(workDays: Set[WorkDay]): ProjectWorkingDay = {
    val projectWorkingDay = new ProjectWorkingDay
    val date = workDays.head.date
    projectWorkingDay.setDate(date)
    val totalWorkSeconds = workDays
      .map(_.workSeconds)
      .sum
    projectWorkingDay.setTotalDayWorkTimeSeconds(totalWorkSeconds)
    val totalIdleSeconds = workDays
      .map(_.idleSeconds)
      .sum
    projectWorkingDay.setTotalIdleTimeSeconds(totalIdleSeconds)
    projectWorkingDay.setTotalIdlePercentage(calculateIdlePercent(totalWorkSeconds, totalIdleSeconds))

    projectWorkingDay.executors = workDays
      .map(workDay =>
        new Executor(
          workDay.author.id,
          workDayService.getStartWorkOf(workDay),
          workDayService.getEndWorkOf(workDay),
          workDay.workSeconds,
          workDay.idleSeconds,
          calculateIdlePercent(workDay.workSeconds, workDay.idleSeconds),
          new Report(
            workDay.getId,
            workDay.project.supervisorId,
            workDay.checked,
            workDay.jailerId,
            workDay.coefficient,
            workDay.reportText,
            workDay.workSeconds,
            workDay.rate,
            workDay.currencyId)
        )
      )

    projectWorkingDay
  }

  def converterToUserWorkingDay(workDaySet: Set[WorkDay]): UserWorkingDay = {
    val workDay = workDaySet.head
    val totalWorkSeconds = workDayService.getTotalWorkSeconds(workDay.getAuthor, workDay.getDate)
    val totalIdleSeconds = workDayService.getTotalIdleSeconds(workDay.getAuthor, workDay.getDate)

    val periods = workDaySet
      .flatMap(workDay => asScalaSet(workDay.getWorkTimePeriods))
      .map(wtp => new Period(
        wtp.getStart,
        wtp.getEnd,
        wtp.getWorkDay.getWorkSeconds,
        wtp.getWorkDay.getIdleSeconds,
        calculateIdlePercent(wtp.getWorkDay.getWorkSeconds, wtp.getWorkDay.getIdleSeconds),
        wtp.getWorkDay.getProject.getSupervisorId,
        wtp.getWorkDay.getRate,
        wtp.getWorkDay.getCurrencyId))

    val reports = workDaySet
      .map(wd => new Report(
        wd.getId,
        wd.getProject.getSupervisorId,
        wd.isChecked,
        wd.getJailerId,
        wd.getCoefficient,
        wd.getReportText,
        wd.getWorkSeconds,
        wd.getRate,
        wd.getCurrencyId))

    new UserWorkingDay(
      workDay.getDate,
      workDaySet
        .map(workDayService.getStartWorkOf)
        .filter(_ != null)
        .reduceOption(minOption)
        .orNull,
      workDaySet
        .map(workDayService.getEndWorkOf)
        .filter(_ != null)
        .reduceOption(maxOption)
        .orNull,
      totalWorkSeconds,
      totalIdleSeconds,
      calculateIdlePercent(totalWorkSeconds, totalIdleSeconds),
      periods,
      reports
    )
  }

  private def minOption(t1: LocalTime, t2: LocalTime) = if (t1 isBefore t2) t1 else t2

  private def maxOption(t1: LocalTime, t2: LocalTime) = if (t1 isAfter t2) t1 else t2

}
