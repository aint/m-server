package ua.softgroup.matrix.server.supervisor.jersey.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.supervisor.jersey.SupervisorEndpoint;
import ua.softgroup.matrix.server.supervisor.jersey.filter.TokenAuthenticationFilter;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(SupervisorEndpoint.class);
        register(GenericExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(ValidationConfigurationContextResolver.class);
        register(TokenAuthenticationFilter.class);

    }

}
