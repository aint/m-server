package ua.softgroup.matrix.server.supervisor.producer.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.token.TokenHelper;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TokenAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationFilter.class);


    private static final String TOKEN = "TOKEN";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = Optional.ofNullable(requestContext.getHeaderString(TOKEN))
                               .orElseThrow(() -> new NotAuthorizedException(new ErrorJson("Authorization header must be provided")));
        try {
            if (!TokenHelper.validateToken(token)) {
                requestContext.abortWith(
                        Response.status(Status.FORBIDDEN).entity(new ErrorJson("Token is not valid")).build());
            }
        } catch (GeneralSecurityException e) {
            LOG.error("Java security-related exception", e);
            requestContext.abortWith(
                    Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorJson("Java security-related exception")).build());
        }

    }
}
