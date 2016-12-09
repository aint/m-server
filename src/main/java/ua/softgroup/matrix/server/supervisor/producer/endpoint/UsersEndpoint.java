package ua.softgroup.matrix.server.supervisor.producer.endpoint;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.TimeAuditRepository;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.TimeJson;

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
import java.util.Optional;

import static ua.softgroup.matrix.server.supervisor.producer.filter.TokenAuthenticationFilter.PRINCIPAL_ID_ATTRIBUTE;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/users")
@Api("users")
public class UsersEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(UsersEndpoint.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkTimeService workTimeService;

    @Autowired
    private TimeAuditRepository timeAuditRepository;
    @Autowired
    private WorkDayRepository workDayRepository;

    @Autowired
    public UsersEndpoint(ProjectService projectService, UserService userService, WorkTimeService workTimeService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workTimeService = workTimeService;
    }

    @GET
    @Path("/{user_id}/{project_id}/time")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    @ApiOperation(
            value = "Returns a today/total work time of the user's project",
            notes = "Showing not relevant response json due to Swagger bug",
            response = TimeJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user/project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    public Response getWorkTime(@Min(0) @PathParam("user_id") Long userId,
                                @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(0L, 0L, project, user));
        return Response.ok(new TimeJson(workTime.getTodayMinutes(), workTime.getTotalMinutes())).build();
    }

    @POST
    @Path("/{user_id}/{project_id}/time")
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
        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(0L, 0L, project, user));
        workTime.setTotalMinutes(workTime.getTotalMinutes() + timeJson.getTotalMinutes());
        workTimeService.save(workTime);

        WorkDay workDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(timeJson.getDate(), workTime))
                                  .orElse(new WorkDay(0L, 0L, workTime));
        workDay.setWorkMinutes(workDay.getWorkMinutes() + timeJson.getTotalMinutes());
        workDayRepository.save(workDay);

        Long principalId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        User principal = userService.getById(principalId).orElseThrow(NotFoundException::new);
        timeAuditRepository.save(new TimeAudit(timeJson.getTotalMinutes(), timeJson.getReason(), principal, workDay));

        return Response.ok(new TimeJson(workTime.getTodayMinutes(), workTime.getTotalMinutes())).build();
    }

}
