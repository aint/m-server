package ua.softgroup.matrix.server.supervisor.jersey;

import io.jsonwebtoken.JwtException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        ErrorJson error = new ErrorJson(
                getStatusType(throwable).getStatusCode(),
                throwable.getLocalizedMessage());

        return Response.status(error.getCode())
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response.StatusType getStatusType(Throwable ex) {
        return (ex instanceof WebApplicationException)
                ? ((WebApplicationException) ex).getResponse().getStatusInfo()
                : (ex instanceof JwtException ? Response.Status.FORBIDDEN : Response.Status.INTERNAL_SERVER_ERROR);
    }
}
