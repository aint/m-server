package ua.softgroup.matrix.server.supervisor.producer.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
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
public class ReportResource {
    private static final Logger LOG = LoggerFactory.getLogger(ReportResource.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    public ReportResource(ProjectService projectService, UserService userService, WorkDayService workDayService) {
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "9. getReport - метод для отримання звіту користувача",
            response = ReportResponse.class)
    public Response getReport(@Min(0) @PathParam("id") Long id) {
        WorkDay workDay = workDayService.getById(id)
                .orElseThrow(NotFoundException::new);

        return Response.ok(convertWorkDayToReportJson(workDay)).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "12. getAllReports - метод для отримання всіх звітів",
            response = ReportResponse.class,
            responseContainer = "List")
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
    @Path("/projects")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "11. getEntitiesReports - метод для отримання звітів",
            response = ReportResponse.class,
            responseContainer = "List")
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "10. getUsersReports - метод для отримання звітів користувачів",
            response = ReportResponse.class,
            responseContainer = "List")
    public Response getReportsOfUsers(@QueryParam("userIds") List<Long> userIds,
                                      @QueryParam("fromDate") String fromDate,
                                      @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        List<ReportResponse> reports = userIds.stream()
                .flatMap(userId -> workDayService.getUserWorkDaysBetween(userId, from, to).stream())
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    private ReportResponse convertWorkDayToReportJson(WorkDay workDay) {
        return new ReportResponse(
                workDay.getId(),
                workDay.getDate(),
                workDay.getReportUpdated(),
                workDay.getAuthor().getId(),
                workDay.getProject().getId(),
                workDay.getJailerId(),
                workDay.isChecked(),
                workDay.getCoefficient(),
                workDay.getReportText(),
                workDay.getWorkSeconds(),
                workDay.getProject().getRate(),
                workDay.getProject().getRateCurrencyId()
        );
    }

    @POST
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "16. checkAllReports - перевірити всі звіти\nChecks all unchecked reports (with default coefficient)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "When all unchecked reports has been successfully checked"),
            @ApiResponse(code = 404, message = "When principal not found", response = ErrorJson.class)
    })
    public Response checkAllReports(@Context ServletContext context) {
        workDayService.getAllNotCheckedWorkDays()
                .forEach(workDay -> {
                    workDay.setJailerId((Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE));
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

    @POST
    @Path("/check/{reportId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "13. checkSingleReport - перевірити звіт\nChecks a report and set coefficient")
    @ApiResponses({
            @ApiResponse(code = 200, message = "When report has been successfully checked"),
            @ApiResponse(code = 400, message = "When report id or coefficient < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When report or principal not found", response = ErrorJson.class)
    })
    public Response checkReport(@Context ServletContext context,
                                @Min(0) @PathParam("reportId") Long reportId,
                                @NotNull @DecimalMin(value = "0") @FormParam("coefficient") Double coefficient) {

        WorkDay workDay = workDayService.getById(reportId).orElseThrow(NotFoundException::new);
        workDay.setJailerId((Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE));
        workDay.setChecked(true);
        workDay.setCoefficient(coefficient);
        workDayService.save(workDay);
        return Response.ok().build();
    }

    @POST
    @Path("/check/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "14. checkAllUsersReports - перевірити звіти вказаних користувачів\nChecks all unchecked reports (with default coefficient) of the specified users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "When reports of the specified users has been successfully checked"),
            @ApiResponse(code = 404, message = "When principal not found", response = ErrorJson.class)
    })
    public Response checkReportsOfUsers(@Context ServletContext context,
                                        List<Long> userIds) {

        userIds.stream()
                .flatMap(userId -> workDayService.getUserNotCheckedWorkDays(userId).stream())
                .forEach(workDay -> {
                    workDay.setJailerId((Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE));
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

    @POST
    @Path("/check/projects")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "15. checkAllEntitiesReports - перевірити звіти вказаних проектів\nChecks all unchecked reports (with default coefficient) of the specified projects")
    @ApiResponses({
            @ApiResponse(code = 200, message = "When reports of the specified projects has been successfully checked"),
            @ApiResponse(code = 404, message = "When principal not found", response = ErrorJson.class)
    })
    public Response checkReportsOfProject(@Context ServletContext context,
                                          List<Long> projectIds) {

        projectIds.stream()
                .flatMap(projectId -> workDayService.getProjectNotCheckedWorkDays(projectId).stream())
                .forEach(workDay -> {
                    workDay.setJailerId((Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE));
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

}
