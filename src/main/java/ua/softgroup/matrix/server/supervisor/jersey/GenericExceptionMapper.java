package ua.softgroup.matrix.server.supervisor.jersey;

import com.nimbusds.jose.JOSEException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.text.ParseException;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        System.out.println("throwable " + throwable);
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
                : (ex instanceof JOSEException || ex instanceof ParseException
                        ? Response.Status.FORBIDDEN
                        : Response.Status.INTERNAL_SERVER_ERROR);
    }
}
