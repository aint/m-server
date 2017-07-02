package ua.softgroup.matrix.server.supervisor.producer.resources

import java.lang
import java.util
import javax.validation.constraints.{DecimalMin, Min}
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import javax.ws.rs.core.{MediaType, Response}

import io.swagger.annotations._
import org.hibernate.validator.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import ua.softgroup.matrix.server.persistent.entity.WorkDay
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService}
import ua.softgroup.matrix.server.supervisor.producer.json.v2.{ErrorJson, ReportResponse}
import ua.softgroup.matrix.server.Utils._

import scala.collection.JavaConverters._

/**
  * This endpoint implements time related functionality.
  * Implements 9, 10, 11, 12, 13, 14, 15 and 16 methods from the Supervisor API specs.
  *
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Component
@Path("/reports")
@Api("/reports")
class ReportResource @Autowired()(userService: UserService,
                                  projectService: ProjectService,
                                  workDayService: WorkDayService) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @GET
  @Path("/{id}")
  @Produces(Array(APPLICATION_JSON))
  @ApiOperation(value = "9) getReport", response = classOf[ReportResponse])
  def getReport(@Min(1) @PathParam("id") id: Long): Response = {
    logger.info(s"Get report $id")

    val workDay = workDayService.getById(id).orElseThrow(() => new NotFoundException)
    Response.ok(convertWorkDayToReportJson(workDay)).build
  }

  @POST
  @Path("/users")
  @Consumes(Array(APPLICATION_FORM_URLENCODED))
  @Produces(Array(APPLICATION_JSON))
  @ApiOperation(value = "10) getUsersReports", response = classOf[ReportResponse], responseContainer = "List")
  def getReportsOfUsers(@ApiParam(example = "[1, 2, 13]") @FormParam("usersIds[]") @NotEmpty userIds: util.List[lang.Long],
                        @ApiParam(example = "2017-01-01") @FormParam("fromDate")   @NotEmpty fromDate: String,
                        @ApiParam(example = "2017-12-31") @FormParam("toDate")     @NotEmpty toDate: String): Response = {

    logger.info(s"Get reports of users $userIds from $fromDate to $toDate")


    val from = fromDate.parseToDate
    val to = toDate.parseToDate
    val result = asScalaBuffer(userIds)
      .filter(_ > 0)
      .flatMap(userId => asScalaSet(workDayService.getUserWorkDaysBetween(userId, from, to)))
      .map(convertWorkDayToReportJson)
      .sortWith(sortByDate)

    Response.ok(result).build
  }

  @POST
  @Path("/project")
  @Consumes(Array(APPLICATION_FORM_URLENCODED))
  @Produces(Array(APPLICATION_JSON))
  @ApiOperation(value = "11) getEntitiesReports", response = classOf[ReportResponse], responseContainer = "List")
  def getReportsOfProjects(@ApiParam(example = "[1, 2, 13]") @FormParam("entitiesIds[]") @NotEmpty projectIds: util.List[lang.Long],
                           @ApiParam(example = "2017-01-01") @FormParam("fromDate")      @NotEmpty fromDate: String,
                           @ApiParam(example = "2017-12-31") @FormParam("toDate")        @NotEmpty toDate: String): Response = {

    logger.info(s"Get reports of projects $projectIds from $fromDate to $toDate")

    val from = fromDate.parseToDate
    val to = toDate.parseToDate

    val result = asScalaBuffer(projectIds)
      .filter(_ > 0)
      .flatMap(projectId => asScalaSet(workDayService.getProjectWorkDaysBetween(projectId, from, to)))
      .map(convertWorkDayToReportJson)
      .sortWith(sortByDate)

    Response.ok(result).build
  }

  @GET
  @Produces(Array(APPLICATION_JSON))
  @ApiOperation(value = "12) getAllReports", response = classOf[ReportResponse], responseContainer = "List")
  def getReports(@ApiParam(example = "2017-01-01") @QueryParam("fromDate") @NotEmpty fromDate: String,
                 @ApiParam(example = "2017-12-31") @QueryParam("toDate")   @NotEmpty toDate: String): Response = {

    logger.info(s"Get ll reports from $fromDate to $toDate")

    val from = fromDate.parseToDate
    val to = toDate.parseToDate

    val result = asScalaSet(workDayService.getWorkDaysBetween(from, to))
      .map(convertWorkDayToReportJson)
      .toList
      .sortWith(sortByDate)

    Response.ok(result).build
  }

  @POST
  @Path("/check/{reportId}")
  @Consumes(Array(APPLICATION_FORM_URLENCODED))
  @ApiOperation("13) checkSingleReport")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "When report id or coefficient < 0", response = classOf[ErrorJson]),
                      new ApiResponse(code = 404, message = "When report not found", response = classOf[ErrorJson])))
  def checkReport(@PathParam("reportId")    @Min(1) reportId: Long,
                  @FormParam("checkedById") @Min(1) checkedById: Long,
                  @FormParam("coefficient") @DecimalMin(value = "0") coefficient: Double): Response = {

    logger.info(s"Check report $reportId with coefficient $coefficient by user $checkedById")

    val workDay = workDayService.getById(reportId).orElseThrow(() => new NotFoundException)
    checkWorkdayAndSave(checkedById, coefficient, workDay)

    Response.ok.build
  }

  @POST
  @Path("/check/users")
  @Consumes(Array(MediaType.APPLICATION_FORM_URLENCODED))
  @ApiOperation("14) checkAllUsersReports")
  def checkReportsOfUsers(@FormParam("usersIds[]")  @NotEmpty userIds: util.List[lang.Long],
                          @FormParam("checkedById") @Min(1)   checkedById: Long,
                          @FormParam("coefficient") @DecimalMin(value = "0") coefficient: Double): Response = {

    logger.info(s"Check reports of users $userIds with coefficient $coefficient by user $checkedById")

    asScalaBuffer(userIds)
      .filter(_ > 0)
      .flatMap(userId => asScalaSet(workDayService.getUserNotCheckedWorkDays(userId)))
      .map(workDay => checkWorkdayAndSave(checkedById, coefficient, workDay))

    Response.ok.build
  }

  @POST
  @Path("/check/project")
  @Consumes(Array(MediaType.APPLICATION_FORM_URLENCODED))
  @ApiOperation("15) checkAllEntitiesReports")
  def checkReportsOfProject(@FormParam("entitiesIds[]") @NotEmpty projectIds: util.List[lang.Long],
                            @FormParam("checkedById")   @Min(1)   checkedById: Long,
                            @FormParam("coefficient")   @DecimalMin(value = "0") coefficient: Double): Response = {

    logger.info(s"Check reports by projects $projectIds with coefficient $coefficient by user $checkedById")

    asScalaBuffer(projectIds)
      .filter(_ > 0)
      .flatMap(projectId => asScalaSet(workDayService.getProjectNotCheckedWorkDays(projectId)))
      .map(workDay => checkWorkdayAndSave(checkedById, coefficient, workDay))

    Response.ok.build
  }

  @POST
  @Path("/check/all")
  @Consumes(Array(MediaType.APPLICATION_FORM_URLENCODED))
  @ApiOperation("16) checkAllReports")
  def checkAllReports(@FormParam("checkedById") @Min(1) checkedById: Long,
                      @FormParam("coefficient") @DecimalMin(value = "0") coefficient: Double): Response = {

    logger.info(s"Check all reports with coefficient $coefficient by user $checkedById")

    workDayService.getAllNotCheckedWorkDays
      .forEach(workDay => checkWorkdayAndSave(checkedById, coefficient, workDay))

    Response.ok.build
  }

  private def checkWorkdayAndSave(checkedById: Long, coefficient: Double, workDay: WorkDay) = {
    workDay.setJailerId(checkedById)
    workDay.setCoefficient(coefficient)
    workDay.setChecked(true)
    workDayService.save(workDay)
  }

  private def sortByDate(r1: ReportResponse, r2: ReportResponse) = r1.date isBefore r2.date

  private def convertWorkDayToReportJson(workDay: WorkDay) =
    new ReportResponse(
      workDay.getId,
      workDay.getDate,
      workDay.getAuthor.getId,
      workDay.getProject.getSupervisorId,
      workDay.getJailerId,
      workDay.isChecked,
      workDay.getCoefficient,
      workDay.getReportText,
      workDay.getWorkSeconds,
      workDay.getProject.getRate,
      workDay.getProject.getRateCurrencyId)

}
