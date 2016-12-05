package ua.softgroup.matrix.server.supervisor.jersey;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.AbstractPeriod;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;
import ua.softgroup.matrix.server.supervisor.jersey.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.jersey.json.JsonViewType;
import ua.softgroup.matrix.server.supervisor.jersey.json.SummaryJson;
import ua.softgroup.matrix.server.supervisor.jersey.json.TimeJson;
import ua.softgroup.matrix.server.supervisor.jersey.token.TokenHelper;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/v1")
public class SupervisorEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(SupervisorEndpoint.class);

    private final ReportService reportService;
    private final ProjectService projectService;
    private final UserService userService;
    private final WorkTimeService workTimeService;

    @Autowired
    private WorkDayRepository workDayRepository;

    @Autowired
    public SupervisorEndpoint(ReportService reportService, ProjectService projectService, UserService userService, WorkTimeService workTimeService) {
        this.reportService = reportService;
        this.projectService = projectService;
        this.userService = userService;
        this.workTimeService = workTimeService;
    }

    @GET
    @Path("/users/{username}/{project_id}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    public Response getReportsOf(@PathParam("username") String username,
                                 @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getByUsername(username).orElseThrow(NotFoundException::new);
        return Response
                .ok(reportService.getAllReportsOf(user, project))
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @PUT
    @Path("/reports/{report_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReport(@Min(0) @PathParam("report_id") Long reportId,
                                 @JsonView(JsonViewType.IN.class) Report reportJson) {
        LOG.info("PUT JSON {}", reportJson);
        Report report = reportService.getById(reportId).orElseThrow(NotFoundException::new);
        report.setTitle(reportJson.getTitle());
        report.setDescription(reportJson.getDescription());
        return Response.ok(reportService.save(report)).build();
    }

    @POST
    @Path("/reports/{report_id}/check")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    public Response checkReport(@HeaderParam("token") String token,
                                @Min(0) @PathParam("report_id") Long reportId,
                                @NotNull @DecimalMin(value = "0") @FormParam("coefficient") Double coefficient) {

        Report report = reportService.getById(reportId).orElseThrow(NotFoundException::new);
        //TODO retrieve principal in token auth filter
        User user = userService.getByUsername(TokenHelper.extractSubjectFromToken(token)).orElseThrow(NotFoundException::new);
        report.getWorkDay().setChecker(user);
        report.getWorkDay().setChecked(true);
        report.getWorkDay().setCoefficient(coefficient);
        workDayRepository.save(report.getWorkDay());
        return Response.ok(reportService.save(report)).build();
    }

    @GET
    @Path("/users/{username}/{project_id}/summary")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getReportsOf(@HeaderParam("token") String token,
                                 @PathParam("username") String username,
                                 @Min(0) @PathParam("project_id") Long projectId) {

        if (!TokenHelper.extractSubjectFromToken(token).equalsIgnoreCase(username)) {
            Response.status(403)
                    .entity(new ErrorJson("Token username and path username not the same"))
                    .build();
        }

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getByUsername(username).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NotFoundException::new);

        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        List<SummaryJson> summary = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, LocalDate.now().plusDays(1)))
                .map(localDate -> workDayRepository.findByDateAndWorkTime(localDate, workTime))
                .filter(Objects::nonNull)
                .map(workDay -> new SummaryJson(
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
                                .orElse(null)))
                .collect(Collectors.toList());
        return Response.ok(summary).build();
    }

    @GET
    @Path("/users/{username}/{project_id}/time")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalTime(@PathParam("username") String username,
                                 @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getByUsername(username).orElseThrow(NotFoundException::new);
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(0, 0, project, user));
        return Response.ok(new TimeJson(workTime.getTodayMinutes().longValue(), workTime.getTotalMinutes().longValue())).build();
    }


}
