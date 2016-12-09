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
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;
import ua.softgroup.matrix.server.supervisor.producer.token.TokenHelper;

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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/reports")
@Api("/reports")
public class ReportsEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ReportsEndpoint.class);

    private final ProjectService projectService;
    private final ReportService reportService;
    private final UserService userService;

    @Autowired
    private WorkDayRepository workDayRepository;

    @Autowired
    public ReportsEndpoint(ProjectService projectService, ReportService reportService, UserService userService) {
        this.projectService = projectService;
        this.reportService = reportService;
        this.userService = userService;
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
            @ApiResponse(code = 400, message = "When project id <= 0", response = ErrorJson.class)
    })
    public Response getReportsOf(@Min(0) @PathParam("user_id") Long userId,
                                 @Min(0) @PathParam("project_id") Long projectId) {

        Project project = projectService.getById(projectId).orElseThrow(NotFoundException::new);
        User user = userService.getById(userId).orElseThrow(NotFoundException::new);
        List<ReportJson> reports = reportService.getAllReportsOf(user, project).stream()
                .map(reportService::convertEntityToJson)
                .collect(Collectors.toList());
        return Response.ok(reports).build();
    }

    @PUT
    @Path("/{report_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update a report by id",
            response = ReportJson.class
    )
    @ApiResponses({
            @ApiResponse(code = 400, message = "When report id <= 0", response = ErrorJson.class)
    })
    public Response updateReport(@Min(0) @PathParam("report_id") Long reportId,
                                 @JsonView(JsonViewType.IN.class) ReportJson reportJson) {
        LOG.info("PUT JSON {}", reportJson);
        Report report = reportService.getById(reportId).orElseThrow(NotFoundException::new);
        report.setTitle(reportJson.getTitle());
        report.setDescription(reportJson.getDescription());
        return Response.ok(reportService.convertEntityToJson(reportService.save(report))).build();
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
            @ApiResponse(code = 400, message = "When report id <= 0 or coefficient <= 0", response = ErrorJson.class),
    })
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
        return Response.ok(reportService.convertEntityToJson(reportService.save(report))).build();
    }


}
