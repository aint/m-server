package ua.softgroup.matrix.server.supervisor.producer.resources;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.DayJson;
import ua.softgroup.matrix.server.supervisor.producer.json.SummaryDayJson;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.softgroup.matrix.server.supervisor.producer.Utils.calculateIdlePercent;
import static ua.softgroup.matrix.server.supervisor.producer.Utils.not;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/summary")
public class DesktopResource {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserService userService;
    private final WorkDayService workDayService;

    public DesktopResource(UserService userService, WorkDayService workDayService) {
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok(userService.getAll()).build();
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
        to = to.isAfter(LocalDate.now()) ? LocalDate.now().plusDays(1) : to;

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
                        workDay.getProject().getId(),
                        workDay.getDate(),
                        workDayService.getStartWorkOf(workDay).toLocalTime(),
                        workDayService.getEndWorkOf(workDay).toLocalTime(),
                        workDay.getWorkSeconds(),
                        workDay.getIdleSeconds(),
                        calculateIdlePercent(workDay.getWorkSeconds(), workDay.getIdleSeconds()),
                        workDay.isChecked(),
                        workDay.getJailerId(),
                        workDay.getCoefficient(),
                        workDay.getReportText(),
                        workDay.getRate(),
                        workDay.getCurrencyId(),
                        workDay.getWorkTimePeriods().stream()
                                .map(wp -> new WorkPeriod(wp.getStart().toLocalTime(), wp.getEnd().toLocalTime()))
                                .collect(Collectors.toSet())))
                .collect(Collectors.toSet()));
        return summaryDayJson;
    }

}
