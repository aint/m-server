package ua.softgroup.matrix.server.supervisor.producer.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.supervisor.producer.json.ErrorJson;
import ua.softgroup.matrix.server.supervisor.producer.token.TokenHelper;

import javax.servlet.ServletContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
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

    public static final String PRINCIPAL_ID_ATTRIBUTE = "principal_id";
    private static final String SWAGGER_JSON = "swagger.json";
    private static final String TOKEN = "TOKEN";

    private ServletContext context;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (SWAGGER_JSON.equals(requestContext.getUriInfo().getPath())) return;

        String token = Optional.ofNullable(requestContext.getHeaderString(TOKEN))
                               .orElseThrow(() -> new NotAuthorizedException(new ErrorJson("Authorization header must be provided")));
        try {
            if (!TokenHelper.validateToken(token)) {
                requestContext.abortWith(
                        Response.status(Status.FORBIDDEN).entity(new ErrorJson("Token is not valid")).build());
            }
            setPrincipalAttribute(token);
        } catch (GeneralSecurityException e) {
            LOG.error("Java security-related exception", e);
            requestContext.abortWith(
                    Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorJson("Java security-related exception")).build());
        }
    }

    private void setPrincipalAttribute(String token) {
        Long subject = Long.valueOf(TokenHelper.extractSubjectFromToken(token));
        LOG.info("Process a request from user {} ", subject);
        context.setAttribute(PRINCIPAL_ID_ATTRIBUTE, subject);
    }

    @Context
    public void setContext(ServletContext context) {
        this.context = context;
    }
}
