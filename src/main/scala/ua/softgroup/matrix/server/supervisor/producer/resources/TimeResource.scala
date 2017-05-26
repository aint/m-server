package ua.softgroup.matrix.server.supervisor.producer.resources

import java.time.LocalDate
import javax.validation.constraints.Min
import javax.ws.rs._
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.{MediaType, Response}

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ua.softgroup.matrix.server.persistent.entity.{Project, User, WorkDay}
import ua.softgroup.matrix.server.service.{ProjectService, UserService, WorkDayService}
import ua.softgroup.matrix.server.supervisor.producer.Utils.{calculateIdlePercent, parseData}
import ua.softgroup.matrix.server.supervisor.producer.json.time.TimeManagement
import ua.softgroup.matrix.server.supervisor.producer.json.{TimeJson, UserProjectTimeResponse, UserTimeResponse}
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ErrorJson

import scala.collection.JavaConverters

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Component
@Path("/times")
@Api("times")
class TimeResource @Autowired() (projectService: ProjectService,
                                 userService: UserService,
                                 workDayService: WorkDayService) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @GET
  @Path("/users/{userId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "6) getUserCommonStatistic", response = classOf[UserProjectTimeResponse], responseContainer = "List")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "When user id < 0", response = classOf[ErrorJson]),
                      new ApiResponse(code = 404, message = "When user not found", response = classOf[ErrorJson])
  ))
  def getUserWorkTime(@ApiParam(example = "42") @Min(0) @PathParam("userId") userId: Long): Response = {
    logger.info(s"getUserCommonStatistic/$userId")

    val response = JavaConverters.asScalaSet(projectService.getUserActiveProjects(userId))
      .map(project => {
        val user = project.getUser
        val workSeconds = workDayService.getTotalWorkSeconds(user, project)
        val idleSeconds = workDayService.getTotalIdleSeconds(user, project)
        val idlePercentage = calculateIdlePercent(workSeconds, idleSeconds)
        new UserProjectTimeResponse(project.getSupervisorId, workSeconds, idleSeconds, idlePercentage)
      })
      .toList

    Response.ok(response).build
  }

  @GET
  @Path("/project/{entityId}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "17) getEntityCommonStatistic", response = classOf[UserTimeResponse], responseContainer = "List")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "When project id < 0", response = classOf[ErrorJson]),
                      new ApiResponse(code = 404, message = "When project not found", response = classOf[ErrorJson])
  ))
  def getProjectWorkTime(@ApiParam(example = "42") @Min(0) @PathParam("entityId") projectId: Long): Response = {
    logger.info(s"getEntityCommonStatistic/$projectId")

    val response = JavaConverters.asScalaSet(projectService.getBySupervisorId(projectId))
      .map(project => {
        val user = project.getUser
        val workSeconds = workDayService.getTotalWorkSeconds(user, project)
        val idleSeconds = workDayService.getTotalIdleSeconds(user, project)
        val idlePercentage = calculateIdlePercent(workSeconds, idleSeconds)
        new UserTimeResponse(user.getId, workSeconds, idleSeconds, idlePercentage)
      })
      .toList

    Response.ok(response).build
  }

  @POST
  @Path("/manage")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(value = "18) manageTime", response = classOf[TimeJson])
  def timeManagement(@ApiParam @JsonView timeManagement: TimeManagement): Response = {
    logger.info(s"manageTime $timeManagement")

    val date = parseData(timeManagement.getOnDate)
    if (date.isAfter(LocalDate.now)) return Response.status(Status.BAD_REQUEST)
                                                    .entity("Date can't be in future")
                                                    .build

    val user = userService.getById(timeManagement.getUserId).orElseThrow(() => new NotFoundException)
    val project = projectService.getBySupervisorIdAndUser(timeManagement.getEntityId, user)
                                .orElseThrow(() => new NotFoundException)
    val workDay = workDayService.getByAuthorAndProjectAndDate(user, project, date)
                                .orElseGet(() => new WorkDay(user, project, date))

    val idlePercentBefore = calculateIdlePercent(workDay.getWorkSeconds, workDay.getIdleSeconds)
    val workSeconds = if ("add" == timeManagement.getAction) workDay.getWorkSeconds + timeManagement.getTime
                      else workDay.getWorkSeconds - timeManagement.getTime
    workDay.setWorkSeconds(workSeconds)

    val idlePercentAfter = calculateIdlePercent(workDay.getWorkSeconds, workDay.getIdleSeconds)
    val idelSeconds = if (timeManagement.getIdleAction == 1) (workDay.getIdleSeconds * idlePercentBefore / idlePercentAfter).toInt
                      else workDay.getIdleSeconds
    workDay.setIdleSeconds(idelSeconds)
    workDay.setReason(timeManagement.getReason)

    workDayService.save(workDay)

//    User principal = userService.getById(principalId).orElseThrow(NotFoundException::new);
//    timeAuditRepository.save(new TimeAudit(timeManagement.getTime(), "reason", principal, workDay));

    Response.ok.build
  }

}
