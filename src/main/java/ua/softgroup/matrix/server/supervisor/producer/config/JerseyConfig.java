package ua.softgroup.matrix.server.supervisor.producer.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.supervisor.producer.MatrixEndpoint;
import ua.softgroup.matrix.server.supervisor.producer.filter.TokenAuthenticationFilter;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MatrixEndpoint.class);
        register(GenericExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(ValidationConfigurationContextResolver.class);
        register(TokenAuthenticationFilter.class);

    }

}
