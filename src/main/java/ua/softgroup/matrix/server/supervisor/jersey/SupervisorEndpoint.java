package ua.softgroup.matrix.server.supervisor.jersey;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.supervisor.jersey.json.JsonViewType;
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

    @Autowired
    public SupervisorEndpoint(ReportService reportService, ProjectService projectService, UserService userService) {
        this.reportService = reportService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GET
    @Path("/projects/{username}/{project_id}/reports")
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
    @Path("/projects/{username}/reports/{report_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReport(@PathParam("username") String username,
                                 @Min(0) @PathParam("report_id") Long reportId,
                                 @JsonView(JsonViewType.IN.class) Report reportJson) {
        LOG.info("PUT JSON {}", reportJson);
        Report report = reportService.getById(reportId).orElseThrow(NotFoundException::new);
        report.setTitle(reportJson.getTitle());
        report.setDescription(reportJson.getDescription());
        return Response.ok(reportService.save(report)).build();
    }

    @POST
    @Path("/projects/{username}/reports/{report_id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViewType.OUT.class)
    public Response checkReport(@HeaderParam("token") String token,
                                @PathParam("username") String username,
                                @Min(0) @PathParam("report_id") Long reportId,
                                @NotNull @DecimalMin(value = "0") @FormParam("coefficient") Double coefficient) {

        Report report = reportService.getById(reportId).orElseThrow(NotFoundException::new);
        //TODO retrieve principal in token auth filter
        User user = userService.getByUsername(TokenHelper.extractSubjectFromToken(token)).orElseThrow(NotFoundException::new);
        report.setChecker(user);
        report.setChecked(true);
        report.setCoefficient(coefficient);
        return Response.ok(reportService.save(report)).build();
    }

}
