package ua.softgroup.matrix.server.supervisor.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(SupervisorEndpoint.class);
        register(GenericExceptionMapper.class);
    }

}
