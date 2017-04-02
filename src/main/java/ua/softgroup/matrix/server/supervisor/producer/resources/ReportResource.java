package ua.softgroup.matrix.server.supervisor.producer.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ReportResponse;

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
import java.util.Optional;
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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserService userService;
    private final ProjectService projectService;
    private final WorkDayService workDayService;

    @Autowired
    public ReportResource(UserService userService, ProjectService projectService, WorkDayService workDayService) {
        this.userService = userService;
        this.projectService = projectService;
        this.workDayService = workDayService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "9) getReport", response = ReportResponse.class)
    public Response getReport(@Min(0) @PathParam("id") Long id) {
        WorkDay workDay = workDayService.getById(id).orElseThrow(NotFoundException::new);
        return Response.ok(convertWorkDayToReportJson(workDay)).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "10) getUsersReports", response = ReportResponse.class, responseContainer = "List")
    public Response getReportsOfUsers(@ApiParam(example = "[1, 2, 13]") @QueryParam("userIds") List<Long> userIds,
                                      @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                      @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        long userCount = userIds.stream()
                .map(userService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .count();

        if ((userCount == 0) && (!userIds.isEmpty())) {
            throw new NotFoundException();
        }

        List<ReportResponse> reports = userIds.stream()
                .flatMap(userId -> workDayService.getUserWorkDaysBetween(userId, from, to).stream())
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "11) getEntitiesReports", response = ReportResponse.class, responseContainer = "List")
    public Response getReportsOfProjects(@ApiParam(example = "[1, 2, 13]") @QueryParam("entityIds") List<Long> projectIds,
                                         @ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                                         @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        long projectCount = projectIds.stream()
                .map(projectService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .count();

        if ((projectCount == 0) && (!projectIds.isEmpty())) {
            throw new NotFoundException();
        }

        List<ReportResponse> reports = projectIds.stream()
                .flatMap(projectId -> workDayService.getProjectWorkDaysBetween(projectId, from, to).stream())
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "12) getAllReports", response = ReportResponse.class, responseContainer = "List")
    public Response getReports(@ApiParam(example = "2017-01-01") @QueryParam("fromDate") String fromDate,
                               @ApiParam(example = "2017-12-31") @QueryParam("toDate") String toDate) {

        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        List<ReportResponse> reports = workDayService.getWorkDaysBetween(from, to).stream()
                .map(this::convertWorkDayToReportJson)
                .collect(Collectors.toList());

        return Response.ok(reports).build();
    }

    private ReportResponse convertWorkDayToReportJson(WorkDay workDay) {
        return new ReportResponse(
                workDay.getId(),
                workDay.getDate(),
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
    @Path("/check/{reportId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("13) checkSingleReport")
    @ApiResponses({
            @ApiResponse(code = 200, message = "When report has been successfully checked"),
            @ApiResponse(code = 400, message = "When report id or coefficient < 0", response = ErrorJson.class),
            @ApiResponse(code = 404, message = "When report not found", response = ErrorJson.class)
    })
    public Response checkReport(@Context ServletContext context,
                                @Min(0) @PathParam("reportId") Long reportId,
                                @NotNull @DecimalMin(value = "0") @FormParam("coefficient") Double coefficient) {

        WorkDay workDay = workDayService.getById(reportId).orElseThrow(NotFoundException::new);
        Long checkerId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        workDay.setJailerId(checkerId == null ? 0L : checkerId);
        workDay.setChecked(true);
        workDay.setCoefficient(coefficient);
        workDayService.save(workDay);
        return Response.ok().build();
    }

    @POST
    @Path("/check/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("14) checkAllUsersReports")
    @ApiResponses(@ApiResponse(code = 200, message = "When reports of the specified users has been successfully checked"))
    public Response checkReportsOfUsers(@Context ServletContext context,
                                        @ApiParam(example = "[1, 2]") List<Long> userIds) {

        Long checkerId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        userIds.stream()
                .flatMap(userId -> workDayService.getUserNotCheckedWorkDays(userId).stream())
                .forEach(workDay -> {
                    workDay.setJailerId(checkerId == null ? 0L : checkerId);
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

    @POST
    @Path("/check/projects")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("15) checkAllEntitiesReports")
    @ApiResponses(@ApiResponse(code = 200, message = "When reports of the specified projects has been successfully checked"))
    public Response checkReportsOfProject(@Context ServletContext context,
                                          @ApiParam(example = "[1, 2]") List<Long> projectIds) {

        Long checkerId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        projectIds.stream()
                .flatMap(projectId -> workDayService.getProjectNotCheckedWorkDays(projectId).stream())
                .forEach(workDay -> {
                    workDay.setJailerId(checkerId == null ? 0L : checkerId);
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

    @POST
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("16) checkAllReports")
    @ApiResponses(@ApiResponse(code = 200, message = "When all unchecked reports has been successfully checked"))
    public Response checkAllReports(@Context ServletContext context) {
        Long checkerId = (Long) context.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
        workDayService.getAllNotCheckedWorkDays()
                .forEach(workDay -> {
                    workDay.setJailerId(checkerId == null ? 0L : checkerId);
                    workDay.setChecked(true);
                    workDayService.save(workDay);
                });

        return Response.ok().build();
    }

}
