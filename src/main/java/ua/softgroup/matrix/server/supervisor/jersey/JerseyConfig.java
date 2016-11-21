package ua.softgroup.matrix.server.supervisor.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@ApplicationPath("/v1")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(SupervisorEndpoint.class);
        register(GenericExceptionMapper.class);
    }

}
