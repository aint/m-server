package ua.softgroup.matrix.server.supervisor.producer.config;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.supervisor.producer.exception.JwtException;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;

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

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable throwable) {
        LOG.warn("Jersey module exception: ", throwable);
        return Response.status(getStatusType(throwable).getStatusCode())
                .entity(new ErrorJson(throwable.getLocalizedMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response.StatusType getStatusType(Throwable ex) {
        return (ex instanceof WebApplicationException)
                ? ((WebApplicationException) ex).getResponse().getStatusInfo()
                : (ex instanceof JOSEException || ex instanceof JwtException
                        ? Response.Status.FORBIDDEN
                        : Response.Status.INTERNAL_SERVER_ERROR);
    }
}
