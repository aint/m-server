package ua.softgroup.matrix.server.supervisor.producer.endpoint;

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
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryJson;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/summary")
@Api("/summary")
public class SummaryEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(SummaryEndpoint.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkTimeService workTimeService;

    @Autowired
    private WorkDayRepository workDayRepository;

    @Autowired
    public SummaryEndpoint(ProjectService projectService, UserService userService, WorkTimeService workTimeService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workTimeService = workTimeService;
    }

    @GET
    @Path("/{user_id}/{project_id}/current")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a daily summary of current month for the user's project",
            response = SummaryJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user/project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    @Transactional
    public Response getSummary(@Min(0) @PathParam("user_id") Long userId,
                               @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().plusDays(1);
        return Response.ok(getSummaryBetween(start, end, workTime)).build();
    }

    @GET
    @Path("/{user_id}/{project_id}/previous")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a daily summary of previous month for the user's project",
            response = SummaryJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user/project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    @Transactional
    public Response getSummaryPreviousMonth(@Min(0) @PathParam("user_id") Long userId,
                                            @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return Response.ok(getSummaryBetween(start, end, workTime)).build();
    }

    private List<SummaryJson> getSummaryBetween(LocalDate start, LocalDate end, WorkTime workTime) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end))
                .map(localDate -> workDayRepository.findByDateAndWorkTime(localDate, workTime))
                .filter(Objects::nonNull)
                .map(workDay -> createSummaryJson(workTime, workDay))
                .collect(Collectors.toList());
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

}
