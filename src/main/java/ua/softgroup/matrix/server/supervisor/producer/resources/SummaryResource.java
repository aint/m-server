package ua.softgroup.matrix.server.supervisor.producer.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.DayJson;
import ua.softgroup.matrix.server.supervisor.producer.json.ExecutorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.ExecutorReportJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryDayJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryProjectJson;
import ua.softgroup.matrix.server.supervisor.producer.json.WorkPeriod;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
public class SummaryResource {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    public SummaryResource(UserService userService, WorkDayService workDayService) {
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/users/{userId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "1) getUserWorkingDays", response = SummaryDayJson.class, responseContainer = "List")
    @Transactional
    public Response getUserWorkingDays(@PathParam("userId") Long userId,
                                       @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                       @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        User user = userService.getById(userId).orElseThrow(NotFoundException::new);

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);
        to = to.isAfter(LocalDate.now()) ? LocalDate.now().plusDays(1) : to;

        List<SummaryDayJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(user, localDate))
                .filter(not(Set::isEmpty))
                .map(this::createSummaryDayJson)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    @GET
    @Path("/projects/{projectId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "2) getEntityWorkingDays", response = SummaryProjectJson.class, responseContainer = "List")
    @Transactional
    public Response getEntityWorkingDays(@PathParam("projectId") Long projectId,
                                         @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                         @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);
        to = to.isAfter(LocalDate.now()) ? LocalDate.now().plusDays(1) : to;

        List<SummaryProjectJson> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(projectId, localDate))
                .filter(not(Set::isEmpty))
                .map(this::createProjectSummary)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private SummaryProjectJson createProjectSummary(Set<WorkDay> workDays) {
        SummaryProjectJson summaryProjectJson = new SummaryProjectJson();
        LocalDate date = workDays.stream()
                .map(WorkDay::getDate)
                .findFirst()
                .orElse(null);
        summaryProjectJson.setDate(date);
        int totalWorkSeconds = workDays.stream()
                .mapToInt(WorkDay::getWorkSeconds)
                .sum();
        summaryProjectJson.setTotalWorkSeconds(totalWorkSeconds);
        int totalIdleSeconds = workDays.stream()
                .mapToInt(WorkDay::getIdleSeconds)
                .sum();
        summaryProjectJson.setTotalIdleSeconds(totalIdleSeconds);
        summaryProjectJson.setTotalIdlePercentage(calculateIdlePercent(totalWorkSeconds, totalIdleSeconds));
        summaryProjectJson.setExecutors(workDays.stream()
                .map(workDay -> new ExecutorJson(
                        workDay.getAuthor().getId(),
                        workDayService.getStartWorkOf(workDay).toLocalTime().toString(),
                        workDayService.getEndWorkOf(workDay).toLocalTime().toString(),
                        workDay.getWorkSeconds(),
                        workDay.getIdleSeconds(),
                        calculateIdlePercent(workDay.getWorkSeconds(), workDay.getIdleSeconds()),
                        new ExecutorReportJson(
                                workDay.getId(),
                                workDay.isChecked(),
                                workDay.getJailerId(),
                                workDay.getCoefficient(),
                                workDay.getReportText(),
                                workDay.getProject().getRate(),                 //TODO move rate to work day
                                workDay.getProject().getRateCurrencyId())))     //TODO move currency to work day
                .collect(Collectors.toSet()));

        return summaryProjectJson;
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
                        workDay.getProject().getId(),
                        workDay.getDate(),
                        workDayService.getStartWorkOf(workDay).toString(),
                        workDayService.getEndWorkOf(workDay).toString(),
                        workDay.getWorkSeconds(),
                        workDay.getIdleSeconds(),
                        calculateIdlePercent(workDay.getWorkSeconds(), workDay.getIdleSeconds()),
                        workDay.isChecked(),
                        workDay.getJailerId(),
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

}
