package ua.softgroup.matrix.server.supervisor.jersey;

import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.persistent.entity.Report;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@Path("/hello")
public class SupervisorEndpoint {

    @PUT
    public String message() {
        return "Hello";
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Report getEventVersion1(@PathParam("id") String id) {
        return new Report(Long.valueOf(id));
    }

}
