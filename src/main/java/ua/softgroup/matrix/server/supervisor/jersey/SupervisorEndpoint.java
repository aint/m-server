package ua.softgroup.matrix.server.supervisor.jersey;

import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Jwts;
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
import ua.softgroup.matrix.server.supervisor.jersey.crypto.KeyHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.NoSuchElementException;

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

    @PUT
    @Path("/reports/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReport(@JsonView(View.IN.class) Report reportJson, @PathParam("id") Long id) {
        LOG.error("report json {}", reportJson);
        Report report = reportService.getById(id).orElseThrow(NoSuchElementException::new);
        report.setTitle(reportJson.getTitle());
        report.setDescription(reportJson.getDescription());
        reportService.save(report);
        return Response
                .status(Status.OK)
                .build();
    }

    @POST
    @Path("/reports")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(View.OUT.class)
    public Response getReportsOf(@HeaderParam("token") String token,
                                 @FormParam("user_id") String userId,
                                 @FormParam("project_id") String projectId) throws IOException, GeneralSecurityException {

        if (!validateJWT(token)) return Response.status(403).entity(new ErrorJson(403, "JWT secure subject not match")).build();

        Project project = projectService.getById(Long.valueOf(projectId)).orElseThrow(NoSuchElementException::new);
        User user = userService.getById(Long.valueOf(userId)).orElseThrow(NoSuchElementException::new);
        return Response
                .status(Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .entity(reportService.getAllReportsOf(user, project))
                .build();
    }

    private boolean validateJWT(String token) throws IOException, GeneralSecurityException {
        String subject = Jwts.parser().setSigningKey(KeyHelper.getKey()).parseClaimsJws(token).getBody().getSubject();
        return KeyHelper.SECRET_WORD.equals(subject);
    }

}
