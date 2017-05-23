package ua.softgroup.matrix.server.supervisor.producer.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.Executor;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.Period;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ProjectWorkingDay;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.Report;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.UserWorkingDay;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.softgroup.matrix.server.supervisor.producer.Utils.calculateIdlePercent;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.not;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.parseData;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.validateEndRangeDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/workdays")
@Api("/workdays")
public class WorkDaysResource {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserService userService;
    private final ProjectService projectService;
    private final WorkDayService workDayService;

    @Autowired
    public WorkDaysResource(UserService userService, ProjectService projectService, WorkDayService workDayService) {
        this.userService = userService;
        this.projectService = projectService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "1) getUserWorkingDays", response = UserWorkingDay.class, responseContainer = "List")
    @Transactional
    public Response userWorkingDaysDno(@ApiParam(example = "14") @Min(0) @PathParam("userId") Long userId,
                                       @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                       @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        User user = userService.getById(userId).orElseGet(User::new);

        LocalDate from = parseData(fromDate);
        LocalDate to = validateEndRangeDate(parseData(toDate));

        List<UserWorkingDay> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(user, localDate))
                .filter(not(Set::isEmpty))
                .map(this::converterToUserWorkingDay)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private UserWorkingDay converterToUserWorkingDay(Set<WorkDay> workDaySet) {
        WorkDay any = workDaySet.stream().findAny().orElseThrow(NoSuchElementException::new);
        int totalWorkSeconds = workDayService.getTotalWorkSeconds(any.getAuthor(), any.getDate());
        int totalIdleSeconds = workDayService.getTotalIdleSeconds(any.getAuthor(), any.getDate());

        Set<Period> periods = workDaySet.stream()
                .flatMap(workDay -> workDay.getWorkTimePeriods().stream())
                .map(wtp -> new Period(
                        wtp.getStart(),
                        wtp.getEnd(),
                        wtp.getWorkDay().getWorkSeconds(),
                        wtp.getWorkDay().getIdleSeconds(),
                        calculateIdlePercent(wtp.getWorkDay().getWorkSeconds(), wtp.getWorkDay().getIdleSeconds()),
                        wtp.getWorkDay().getProject().getSupervisorId(),
                        wtp.getWorkDay().getRate(),
                        wtp.getWorkDay().getCurrencyId()
                ))
                .collect(Collectors.toSet());

        Set<Report> reports = workDaySet.stream()
                .map(wd -> new Report(
                        wd.getId(),
                        wd.getProject().getSupervisorId(),
                        wd.isChecked(),
                        wd.getJailerId(),
                        wd.getCoefficient(),
                        wd.getReportText(),
                        wd.getWorkSeconds(),
                        wd.getRate(),
                        wd.getCurrencyId()

                ))
                .collect(Collectors.toSet());

        return new UserWorkingDay(
                any.getDate(),
                workDaySet.stream()
                        .map(workDayService::getStartWorkOf)
                        .filter(Objects::nonNull)
                        .min(LocalTime::compareTo)
                        .orElse(null),
                workDaySet.stream()
                        .map(workDayService::getEndWorkOf)
                        .filter(Objects::nonNull)
                        .max(LocalTime::compareTo)
                        .orElse(null),
                totalWorkSeconds,
                totalIdleSeconds,
                calculateIdlePercent(totalWorkSeconds, totalIdleSeconds),
                periods,
                reports
        );
    }

    @GET
    @Path("/project/{entityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "2) getEntityWorkingDays", response = ProjectWorkingDay.class, responseContainer = "List")
    @Transactional
    public Response getEntityWorkingDays(@ApiParam(example = "14") @Min(0) @PathParam("entityId") Long projectId,
                                         @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                         @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        LocalDate from = parseData(fromDate);
        LocalDate to = validateEndRangeDate(parseData(toDate));

        List<ProjectWorkingDay> result = Stream.iterate(from, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to))
                .map(localDate -> workDayService.getAllWorkDaysOf(projectId, localDate))
                .filter(not(Set::isEmpty))
                .map(this::convertToProjectWorkingDay)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    private ProjectWorkingDay convertToProjectWorkingDay(Set<WorkDay> workDays) {
        ProjectWorkingDay projectWorkingDay = new ProjectWorkingDay();
        LocalDate date = workDays.stream()
                .map(WorkDay::getDate)
                .findFirst()
                .orElse(null);
        projectWorkingDay.setDate(date);
        int totalWorkSeconds = workDays.stream()
                .mapToInt(WorkDay::getWorkSeconds)
                .sum();
        projectWorkingDay.setTotalDayWorkTimeSeconds(totalWorkSeconds);
        int totalIdleSeconds = workDays.stream()
                .mapToInt(WorkDay::getIdleSeconds)
                .sum();
        projectWorkingDay.setTotalIdleTimeSeconds(totalIdleSeconds);
        projectWorkingDay.setTotalIdlePercentage(calculateIdlePercent(totalWorkSeconds, totalIdleSeconds));
        projectWorkingDay.setExecutors(workDays.stream()
                .map(workDay -> new Executor(
                        workDay.getAuthor().getId(),
                        workDayService.getStartWorkOf(workDay),
                        workDayService.getEndWorkOf(workDay),
                        workDay.getWorkSeconds(),
                        workDay.getIdleSeconds(),
                        calculateIdlePercent(workDay.getWorkSeconds(), workDay.getIdleSeconds()),
                        new Report(
                                workDay.getId(),
                                workDay.getProject().getSupervisorId(),
                                workDay.isChecked(),
                                workDay.getJailerId(),
                                workDay.getCoefficient(),
                                workDay.getReportText(),
                                workDay.getWorkSeconds(),
                                workDay.getRate(),
                                workDay.getCurrencyId())))
                .collect(Collectors.toSet()));

        return projectWorkingDay;
    }

}
