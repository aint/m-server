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
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.AbstractPeriod;
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
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryJson;
import ua.softgroup.matrix.server.supervisor.producer.json.TimeJson;
import ua.softgroup.matrix.server.supervisor.producer.token.TokenHelper;

import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Path("/{user_id}/{project_id}/summary")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @ApiOperation(
            value = "Returns a daily summary of current month for the user's project",
            response = SummaryJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user or project ids <= 0", response = ErrorJson.class)
    })
    public Response getReportsOf(@HeaderParam("token") String token,
                                 @Min(0) @PathParam("user_id") Long userId,
                                 @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        List<SummaryJson> summary = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, LocalDate.now().plusDays(1)))
                .map(localDate -> workDayRepository.findByDateAndWorkTime(localDate, workTime))
                .filter(Objects::nonNull)
                .map(workDay -> createSummaryJson(workTime, workDay))
                .collect(Collectors.toList());
        return Response.ok(summary).build();
    }

    private SummaryJson createSummaryJson(WorkTime workTime, WorkDay workDay) {
        return new SummaryJson(
                workDay.getDate(),
                workDay.getWorkMinutes(),
                workDay.getIdleMinutes(),
                workTime.getRate(),
                workTime.getRateCurrencyId(),
                workDay.isChecked(),
                workDay.getCoefficient(),
                workDay.getWorkTimePeriods().stream()
                        .map(AbstractPeriod::getStart)
                        .min(LocalDateTime::compareTo)
                        .orElse(null),
                workDay.getWorkTimePeriods().stream()
                        .map(AbstractPeriod::getEnd)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)
        );

    }

    @GET
    @Path("/{user_id}/{project_id}/time")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a today/total work time of the user's project",
            response = TimeJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user or project ids <= 0", response = ErrorJson.class)
    })
    public Response getTotalTime(@Min(0) @PathParam("user_id") Long userId,
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
            response = TimeJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user or project ids <= 0", response = ErrorJson.class)
    })
    public Response addTime(@HeaderParam("token") String token,
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

        User principal = userService.getByUsername(TokenHelper.extractSubjectFromToken(token)).orElseThrow(NotFoundException::new);
        timeAuditRepository.save(new TimeAudit(timeJson.getTotalMinutes(), timeJson.getReason(), principal, workDay));

        return Response.ok(new TimeJson(workTime.getTodayMinutes(), workTime.getTotalMinutes())).build();
    }

}
