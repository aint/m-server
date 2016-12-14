package ua.softgroup.matrix.server.supervisor.producer.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import java.util.Optional;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
public class TokenAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    public static final String PRINCIPAL_ID_ATTRIBUTE = "principal_id";
    private static final String SWAGGER_JSON = "swagger.json";
    private static final String TOKEN = "TOKEN";

    private final TokenHelper tokenHelper;
    private ServletContext context;

    @Autowired
    public TokenAuthenticationFilter(TokenHelper tokenHelper) {
        this.tokenHelper = tokenHelper;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (SWAGGER_JSON.equals(requestContext.getUriInfo().getPath())) return;

        String token = Optional.ofNullable(requestContext.getHeaderString(TOKEN))
                               .orElseThrow(() -> new NotAuthorizedException(new ErrorJson("Authorization header must be provided")));

        if (!tokenHelper.validateToken(token)) {
            requestContext.abortWith(
                    Response.status(Status.FORBIDDEN).entity(new ErrorJson("Token is not valid")).build());
        }
        setPrincipalAttribute(token);
    }

    private void setPrincipalAttribute(String token) {
        Long subject = Long.valueOf(tokenHelper.extractSubjectFromToken(token));
        LOG.info("Process a request from user {} ", subject);
        context.setAttribute(PRINCIPAL_ID_ATTRIBUTE, subject);
    }

    @Context
    public void setContext(ServletContext context) {
        this.context = context;
    }
}
