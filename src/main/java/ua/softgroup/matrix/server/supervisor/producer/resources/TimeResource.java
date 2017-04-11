package ua.softgroup.matrix.server.supervisor.producer.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.TimeAudit;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.TimeAuditRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.TimeJson;
import ua.softgroup.matrix.server.supervisor.producer.json.UserProjectTimeResponse;
import ua.softgroup.matrix.server.supervisor.producer.json.UserTimeResponse;

import javax.servlet.ServletContext;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static ua.softgroup.matrix.server.supervisor.producer.Utils.calculateIdlePercent;
import static ua.softgroup.matrix.server.supervisor.producer.filter.TokenAuthenticationFilter.PRINCIPAL_ID_ATTRIBUTE;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/times")
@Api("times")
public class TimeResource {
    private static final Logger LOG = LoggerFactory.getLogger(TimeResource.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    private TimeAuditRepository timeAuditRepository;

    @Autowired
    public TimeResource(ProjectService projectService, UserService userService, WorkDayService workDayService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/project/{entityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "17) getEntityCommonStatistic", response = UserTimeResponse.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 400, message = "When project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When project not found", response = ErrorJson.class)
    })
    public Response getProjectWorkTime(@ApiParam(example = "project") @Min(0) @PathParam("entityId") Long projectId) {

        if (projectService.getBySupervisorId(projectId).isEmpty()) {
            throw new NotFoundException();
        }

        List<UserTimeResponse> timeList = projectService.getBySupervisorId(projectId).stream()
                .map(project -> {
                    User user = project.getUser();
                    int workSeconds = workDayService.getTotalWorkSeconds(user, project);
                    int idleSeconds = workDayService.getTotalIdleSeconds(user, project);
                    double idlePercentage = calculateIdlePercent(workSeconds, idleSeconds);
                    return new UserTimeResponse(user.getId(), workSeconds, idleSeconds, idlePercentage);
                })
                .collect(Collectors.toList());

        return Response.ok(timeList).build();
    }

    @GET
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "6) getUserCommonStatistic", response = UserProjectTimeResponse.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user not found", response = ErrorJson.class)
    })
    public Response getUserWorkTime(@Min(0) @PathParam("userId") Long userId) {
        userService.getById(userId).orElseThrow(NotFoundException::new);
        List<UserProjectTimeResponse> timeList = projectService.getUserActiveProjects(userId).stream()
                .map(project -> {
                    User user = project.getUser();
                    int workSeconds = workDayService.getTotalWorkSeconds(user, project);
                    int idleSeconds = workDayService.getTotalIdleSeconds(user, project);
                    double idlePercentage = calculateIdlePercent(workSeconds, idleSeconds);
                    return new UserProjectTimeResponse(project.getId(), workSeconds, idleSeconds, idlePercentage);
                })
                .collect(Collectors.toList());

        return Response.ok(timeList).build();
    }

    @POST
    @Path("/{user_id}/{project_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    @ApiOperation(
            value = "Add a work time for the user's project",
            notes = "Showing not relevant response/request json due to Swagger bug",
            response = TimeJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user/project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    public Response addWorkTime(@Context ServletContext context,
                                @Min(0) @PathParam("user_id") Long userId,
                                @Min(0) @PathParam("project_id") Long projectId,
                                @JsonView(JsonViewType.IN.class) TimeJson timeJson) {

        LOG.info("POST JSON {}", timeJson);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        Project project = projectService.getBySupervisorIdAndUser(projectId, user).orElseThrow(NotFoundException::new);

        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, timeJson.getDate())
                                        .orElse(new WorkDay(user, project, timeJson.getDate()));
        workDay.setWorkSeconds(workDay.getWorkSeconds() + timeJson.getTotalMinutes());
        workDayService.save(workDay);

        Long principalId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        User principal = userService.getById(principalId).orElseThrow(NotFoundException::new);
        timeAuditRepository.save(new TimeAudit(timeJson.getTotalMinutes(), timeJson.getReason(), principal, workDay));

        int totalWorkSeconds = workDayService.getTotalWorkSeconds(user, project);

        return Response.ok(new TimeJson(workDay.getWorkSeconds(), totalWorkSeconds)).build();
    }

}
