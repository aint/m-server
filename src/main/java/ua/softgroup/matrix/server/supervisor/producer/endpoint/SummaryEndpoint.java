package ua.softgroup.matrix.server.supervisor.producer.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.DayJson;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryDayJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryJson;
import ua.softgroup.matrix.server.supervisor.producer.json.WorkPeriod;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/summary")
@Api("/summary")
public class SummaryEndpoint {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    public SummaryEndpoint(ProjectService projectService, UserService userService, WorkDayService workDayService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/users/{userId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUserWorkingDays(@PathParam("userId") Long userId,
                                       @QueryParam("fromDate") String fromDate,
                                       @QueryParam("toDate") String toDate) {

        User user = userService.getById(userId).orElseThrow(NotFoundException::new);

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);
        if (to.isAfter(LocalDate.now())) {
            to = LocalDate.now().plusDays(1);
        }

        List<SummaryDayJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(user, localDate))
                .filter(not(Set::isEmpty))
                .map(this::createSummaryDayJson)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private SummaryDayJson createSummaryDayJson(Set<WorkDay> workDays) {
        SummaryDayJson summaryDayJson = new SummaryDayJson();
        LocalDate date = workDays.stream()
                                 .map(WorkDay::getDate)
                                 .findFirst()
                                 .orElse(null);
        summaryDayJson.setDate(date);
        summaryDayJson.setTotalWorkSeconds(workDays.stream().mapToInt(WorkDay::getWorkSeconds).sum());
        summaryDayJson.setTotalIdleSeconds(workDays.stream().mapToInt(WorkDay::getIdleSeconds).sum());
        summaryDayJson.setTotalIdlePercentage(calculateIdlePercent(summaryDayJson.getTotalWorkSeconds(), summaryDayJson.getTotalIdleSeconds()));
        summaryDayJson.setWorkDays(workDays.stream()
                .map(workDay -> new DayJson(
                        workDay.getId(),
                        workDay.getDate(),
                        workDayService.getStartWorkOf(workDay).toString(),
                        workDayService.getEndWorkOf(workDay).toString(),
                        workDay.getWorkSeconds(),
                        workDay.getIdleSeconds(),
                        calculateIdlePercent(workDay.getWorkSeconds(), workDay.getIdleSeconds()),
                        workDay.isChecked(),
                        workDay.getChecker() == null ? 0 : workDay.getChecker().getId(),
                        workDay.getCoefficient(),
                        workDay.getReportText(),
                        workDay.getProject().getRate(),           //TODO move rate to work day
                        workDay.getProject().getRateCurrencyId(), //TODO move currency to work day
                        workDay.getWorkTimePeriods().stream()
                                .map(wp -> new WorkPeriod(wp.getStart().toString(), wp.getEnd().toString()))
                                .collect(Collectors.toSet())))
                .collect(Collectors.toSet()));
        return summaryDayJson;
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    private double calculateIdlePercent(int workSeconds, int idleSeconds) {
        return idleSeconds != 0
                ? idleSeconds / workSeconds * 100
                : 0.0;
    }

    @GET
    @Path("/{user_id}/{project_id}/months/{months_number}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a daily summary for a specified number of months of user's project",
            response = SummaryJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When userId/projectId/months < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    @Transactional
    public Response getSummaryOfMonth(@Min(0) @PathParam("user_id") Long userId,
                                      @Min(0) @PathParam("project_id") Long projectId,
                                      @Min(0) @PathParam("months_number") Long months) {

        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        Project project = projectService.getBySupervisorIdAndUser(projectId, user).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().minusMonths(months).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = (months == 0)
                ? LocalDate.now().plusDays(1)
                : LocalDate.now().minusMonths(months).with(TemporalAdjusters.lastDayOfMonth());
        return Response.ok(getSummaryBetween(start, end, user, project)).build();
    }

    @GET
    @Path("/{user_id}/{project_id}/days/{days_number}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a daily summary for a specified number of days of the user's project",
            response = SummaryJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When userId/projectId/days < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    @Transactional
    public Response getSummaryOfDays(@Min(0) @PathParam("user_id") Long userId,
                                     @Min(0) @PathParam("project_id") Long projectId,
                                     @Min(0) @PathParam("days_number") Long days) {

        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        Project project = projectService.getBySupervisorIdAndUser(projectId, user).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = (days == 0)
                ? LocalDate.now().plusDays(1)
                : LocalDate.now();
        return Response.ok(getSummaryBetween(start, end, user, project)).build();
    }

    private List<SummaryJson> getSummaryBetween(LocalDate start, LocalDate end, User user, Project project) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end))
                .map(localDate -> workDayService.getByAuthorAndProjectAndDate(user, project, localDate))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(workDay -> createSummaryJson(project, workDay))
                .collect(Collectors.toList());
    }

    private SummaryJson createSummaryJson(Project project, WorkDay workDay) {
        return new SummaryJson(
                workDay.getDate(),
                workDay.getWorkSeconds(),
                workDay.getIdleSeconds(),
                project.getRate(),
                project.getRateCurrencyId(),
                workDay.isChecked(),
                workDay.getCoefficient(),
                workDay.getWorkTimePeriods().stream()
                        .map(WorkTimePeriod::getStart)
                        .min(LocalDateTime::compareTo)
                        .orElse(null),
                workDay.getWorkTimePeriods().stream()
                        .map(WorkTimePeriod::getEnd)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)
        );

    }

}
