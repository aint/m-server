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
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportResponse;

import javax.servlet.ServletContext;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ua.softgroup.matrix.server.supervisor.producer.filter.TokenAuthenticationFilter.PRINCIPAL_ID_ATTRIBUTE;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/reports")
@Api("/reports")
public class ReportsEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ReportsEndpoint.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    public ReportsEndpoint(ProjectService projectService, UserService userService, WorkDayService workDayService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReports(@QueryParam("fromDate") String fromDate,
                               @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        List<ReportResponse> reports = workDayService.getWorkDaysBetween(from, to).stream()
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@Min(0) @PathParam("id") Long id) {
        WorkDay workDay = workDayService.getById(id)
                                        .orElseThrow(NotFoundException::new);

        return Response.ok(convertWorkDayToReportJson(workDay)).build();
    }

    private ReportResponse convertWorkDayToReportJson(WorkDay workDay) {
        return new ReportResponse(
                workDay.getId(),
                workDay.getDate(),
                workDay.getReportUpdated(),
                workDay.getAuthor().getId(),
                workDay.getProject().getId(),
                workDay.getChecker().getId(),
                workDay.isChecked(),
                workDay.getCoefficient(),
                workDay.getReportText(),
                workDay.getWorkSeconds(),
                workDay.getProject().getRate(),
                workDay.getProject().getRateCurrencyId()
        );
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportsOfProjects(@QueryParam("projectIds") List<Long> projectIds,
                                         @QueryParam("fromDate") String fromDate,
                                         @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        List<ReportResponse> reports = projectIds.stream()
                .flatMap(projectId -> workDayService.getProjectWorkDaysBetween(projectId, from, to).stream())
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportsOfUsers(@QueryParam("usersIds") List<Long> usersIds,
                                      @QueryParam("fromDate") String fromDate,
                                      @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        List<ReportResponse> reports = usersIds.stream()
                .flatMap(userId -> workDayService.getUserWorkDaysBetween(userId, from, to).stream())
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }


    @GET
    @Path("/{user_id}/{project_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    @ApiOperation(
            value = "Returns reports of the user's project",
            response = ReportJson.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When user/project id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When user/project not found", response = ErrorJson.class)
    })
    public Response getReports(@Min(0) @PathParam("user_id") Long userId,
                               @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        List<ReportJson> reports = workDayService.getAllWorkDaysOf(user, project).stream()
                .map(workDayService::convertEntityToJson)
                .collect(Collectors.toList());
        return Response.ok(reports).build();
    }

    @PUT
    @Path("/{report_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update a report by id",
            notes = "Showing not relevant request json due to Swagger bug",
            response = ReportJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When report id < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When report not found", response = ErrorJson.class)
    })
    public Response updateReport(@Min(0) @PathParam("report_id") Long reportId,
                                 @JsonView(JsonViewType.IN.class) ReportJson reportJson) {
        LOG.info("PUT JSON {}", reportJson);
        WorkDay workDay = workDayService.getById(reportId).orElseThrow(NotFoundException::new);
        workDay.setReportText(reportJson.getTitle());
        return Response.ok(workDayService.convertEntityToJson(workDayService.save(workDay))).build();
    }

    @POST
    @Path("/{report_id}/check")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    @ApiOperation(
            value = "Checks a report and set coefficient",
            response = ReportJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When report id or coefficient < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When report or principal not found", response = ErrorJson.class)
    })
    public Response checkReport(@Context ServletContext context,
                                @Min(0) @PathParam("report_id") Long reportId,
                                @NotNull @DecimalMin(value = "0") @FormParam("coefficient") Double coefficient) {

        Long principalId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        User principal = userService.getById(principalId).orElseThrow(NotFoundException::new);
        WorkDay workDay = workDayService.getById(reportId).orElseThrow(NotFoundException::new);
        workDay.setChecker(principal);
        workDay.setChecked(true);
        workDay.setCoefficient(coefficient);
        return Response.ok(workDayService.convertEntityToJson(workDayService.save(workDay))).build();
    }


}
