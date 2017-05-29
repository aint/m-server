package ua.softgroup.matrix.server.supervisor.producer.resources

import java.nio.file.{Files, Paths}
import java.time.temporal.ChronoUnit
import java.util
import java.util.Base64.getEncoder
import javax.validation.constraints.Min
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import org.hibernate.validator.constraints.NotBlank
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ua.softgroup.matrix.server.persistent.entity.{Screenshot, TrackingData, WorkDay, WorkTimePeriod}
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService}
import ua.softgroup.matrix.server.supervisor.producer.Utils.{parseData, validateEndRangeDate}
import ua.softgroup.matrix.server.supervisor.producer.json.tracking.{GeneralWorkDataJson, TrackingDataJson, TrackingDataViewType, TrackingPeriodJson}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Component
@Path("/tracking")
@Api("/tracking")
class TrackingDataResourc @Autowired()(userService: UserService,
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

  private def convertToProjectTrackingData(workDays: Set[WorkDay]): TrackingDataJson = {
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
  }

  private def convertWorkTimePeriods(workTimePeriods: Set[WorkTimePeriod]): util.List[TrackingPeriodJson] = {
    seqAsJavaList(workTimePeriods.toStream
      .sortWith(sortByDate)
      .map(period => new TrackingPeriodJson(
        period.start,
        period.end,
        getTrackingData(period).keyboardText,
        convertRandomScreenshotToBase64(getTrackingData(period).screenshots) getOrElse new Array[String](2)))
      .toList)
  }

  private def sortByDate(p1: WorkTimePeriod, p2: WorkTimePeriod) = p1.start isBefore p2.start

  private def getTrackingData(workTimePeriod: WorkTimePeriod) = {
    if (workTimePeriod.getTrackingData != null) workTimePeriod.getTrackingData else new TrackingData
  }

  private def convertRandomScreenshotToBase64(screenshots: util.Set[Screenshot]): Try[Array[String]] = {
    val result = new Array[String](2)
    screenshots.stream
      .findAny
      .ifPresent(screenshot => {
        val imageBytes = Files.readAllBytes(Paths.get(screenshot.path))
        result(0) = "data:image/png;base64," + getEncoder.encodeToString(imageBytes)
        result(1) = screenshot.screenshotTitle
      })

    Try(result)
  }

}
