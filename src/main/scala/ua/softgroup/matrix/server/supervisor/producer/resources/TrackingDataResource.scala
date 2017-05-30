package ua.softgroup.matrix.server.supervisor.producer.resources

import java.io.IOException
import java.nio.file.{Files, Paths}
import java.lang
import java.time.temporal.ChronoUnit
import java.util
import java.util.Base64.getEncoder
import javax.validation.constraints.Min
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import org.hibernate.validator.constraints.{NotBlank, NotEmpty}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ua.softgroup.matrix.server.persistent.entity._
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService}
import ua.softgroup.matrix.server.supervisor.producer.Utils.{calculateIdlePercent, parseData, validateEndRangeDate}
import ua.softgroup.matrix.server.supervisor.producer.json.UserTimeAndCountResponse
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.{GeneralWorkDataJson, TrackingDataJson, TrackingDataViewType, TrackingPeriodJson}

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Component
@Path("/tracking")
@Api("/tracking")
class TrackingDataResource @Autowired()(userService: UserService,
                                        projectService: ProjectService,
                                        workDayService: WorkDayService) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @GET
  @Path("/project/{entityId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "3) getEntityControlData", response = classOf[TrackingDataJson], responseContainer = "List")
  @Transactional
  @JsonView(Array(classOf[TrackingDataViewType.USER]))
  def getTrackingDataByProject(@ApiParam(example = "14") @Min(1) @PathParam("entityId")  projectId: Long,
                               @ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotBlank fromDate: String,
                               @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotBlank toDate: String): Response = {

    logger.info(s"Get tracking data of project $projectId from $fromDate to $toDate")

    val from = parseData(fromDate)
    val to = validateEndRangeDate(parseData(toDate))

    val result = (0 until ChronoUnit.DAYS.between(from, to).toInt).toStream
      .map(index => workDayService.getAllWorkDaysOf(projectId, from.plusDays(index)))
      .filterNot(_.isEmpty)
      .map(s => convertToProjectTrackingData(asScalaSet(s).toSet))
      .toList

    Response.ok(result).build
  }

  @GET
  @Path("/project/{entityId}/users/{userId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "4) getEntityUserControlData", response = classOf[TrackingDataJson], responseContainer = "List")
  @Transactional
  @JsonView(Array(classOf[TrackingDataViewType.DATE]))
  def getTrackingDataByProjectAndUser(@ApiParam(example = "14") @Min(0) @PathParam("entityId")  projectId: Long,
                                      @ApiParam(example = "14") @Min(0) @PathParam("userId")    userId: Long,
                                      @ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotBlank fromDate: String,
                                      @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotBlank toDate: String): Response = {

    logger.info(s"Get tracking data of user $userId and project $projectId from $fromDate to $toDate")

    val from = parseData(fromDate)
    val to = parseData(toDate)

    val result = asScalaSet(workDayService.getAllWorkDaysOf(userId, projectId, from, to))
      .map(convertToUserAndProjectTrackingData)
      .toList

    Response.ok(result).build
  }

  @GET
  @Path("/users/{userId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "5) getUserControlData", response = classOf[TrackingDataJson], responseContainer = "List")
  @Transactional
  @JsonView(Array(classOf[TrackingDataViewType.PROJECT]))
  def getTrackingDataByUser(@ApiParam(example = "14") @Min(0) @PathParam("userId")    userId: Long,
                            @ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotBlank fromDate: String,
                            @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotBlank toDate: String): Response = {

    logger.info(s"Get tracking data of user $userId from $fromDate to $toDate")

    val user = userService.getById(userId).orElseGet(() => new User)
    val from = parseData(fromDate)
    val to = validateEndRangeDate(parseData(toDate))

    val result = (0 until ChronoUnit.DAYS.between(from, to).toInt).toStream
      .map(index => workDayService.getAllWorkDaysOf(user, from.plusDays(index)))
      .filterNot(_.isEmpty)
      .map(s => convertToUserTrackingData(asScalaSet(s).toSet))
      .toList

    Response.ok(result).build
  }

  @POST
  @Path("/users/")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "8) getFeaturedTogetherControlData", response = classOf[UserTimeAndCountResponse], responseContainer = "List")
  def getFeaturedTogetherControlData(@ApiParam(example = "[1, 2, 13]") @FormParam("usersIds[]") @NotEmpty userIds: util.List[lang.Long],
                                     @ApiParam(example = "2017-01-01") @FormParam("fromDate")   @NotBlank fromDate: String,
                                     @ApiParam(example = "2017-12-31") @FormParam("toDate")     @NotBlank toDate: String): Response = {

    logger.info(s"Get tracking data of users $userIds from $fromDate to $toDate")

    val from = parseData(fromDate)
    val to = parseData(toDate)

    val result = asScalaBuffer(userIds)
      .filter(_ > 0)
      .map(userId => {
        val workSeconds = workDayService.getTotalWorkSeconds(userId, from, to)
        val idleSeconds = workDayService.getTotalIdleSeconds(userId, from, to)
        new UserTimeAndCountResponse(
          userId,
          workSeconds,
          idleSeconds,
          calculateIdlePercent(workSeconds, idleSeconds),
          workDayService.getSymbolsCount(userId, from, to),
          workDayService.getWindowsSwitchedCount(userId, from, to))
      })

    Response.ok(result).build
  }

  private def convertToUserTrackingData(workDays: Set[WorkDay]): TrackingDataJson =
    new TrackingDataJson(
      workDays.head.date,
      workDays
        .map(workDay => new GeneralWorkDataJson(
          workDay.project.supervisorId,
          "project",
          workDayService.getStartWorkOf(workDay),
          workDayService.getEndWorkOf(workDay),
          workDay.workSeconds,
          convertWorkTimePeriods(asScalaSet(workDay.workTimePeriods).toSet)))
        .toList
        .asJava
    )

  private def convertToUserAndProjectTrackingData(workDay: WorkDay) =
    new GeneralWorkDataJson(
      workDay.getDate,
      workDayService.getStartWorkOf(workDay),
      workDayService.getEndWorkOf(workDay),
      workDay.getWorkSeconds,
      convertWorkTimePeriods(asScalaSet(workDay.workTimePeriods).toSet)
    )

  private def convertToProjectTrackingData(workDays: Set[WorkDay]) =
    new TrackingDataJson(
      workDays.head.date,
      workDays
        .map(workDay => new GeneralWorkDataJson(
          workDay.author.id,
          workDayService.getStartWorkOf(workDay),
          workDayService.getEndWorkOf(workDay),
          workDay.workSeconds,
          convertWorkTimePeriods(asScalaSet(workDay.workTimePeriods).toSet)))
        .toList
        .asJava
    )

  private def convertWorkTimePeriods(workTimePeriods: Set[WorkTimePeriod]): util.List[TrackingPeriodJson] = {
    seqAsJavaList(workTimePeriods.toStream
      .sortWith(sortByDate)
      .map(period => new TrackingPeriodJson(
        period.start,
        period.end,
        getTrackingData(period).keyboardText,
        logTry(convertRandomScreenshotToBase64(getTrackingData(period).screenshots)) getOrElse new Array[String](2)))
      .toList)
  }

  private def sortByDate(p1: WorkTimePeriod, p2: WorkTimePeriod) = p1.start isBefore p2.start

  private def getTrackingData(workTimePeriod: WorkTimePeriod) = {
    if (workTimePeriod.getTrackingData != null) workTimePeriod.getTrackingData else new TrackingData
  }

  private def convertRandomScreenshotToBase64(screenshots: util.Set[Screenshot]): Array[String] = {
    val result = new Array[String](2)
    screenshots.stream
      .findAny
      .ifPresent(screenshot => {
        val imageBytes = Files.readAllBytes(Paths.get(screenshot.path))
        result(0) = "data:image/png;base64," + getEncoder.encodeToString(imageBytes)
        result(1) = screenshot.screenshotTitle
      })

    result
  }

  private def logTry[A](computation: => A): Try[A] = {
    Try(computation) recoverWith {
      case e: IOException =>
        logger.error("Failed to encode screenshot to Base64", e)
        Failure(e)
    }
  }

}
