package ua.softgroup.matrix.server.supervisor.producer.config;

import org.hibernate.validator.internal.engine.path.PathImpl;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException e) {
        String message = ((ConstraintViolationException) e).getConstraintViolations().stream()
                .map(cv -> ((PathImpl) cv.getPropertyPath()).getLeafNode().getName() + " " + cv.getMessage())
                .collect(Collectors.joining("; "));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorJson(message))
                .build();
    }

}
