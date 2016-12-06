package ua.softgroup.matrix.server.supervisor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.server.supervisor.endpoint.SupervisorQueries;

@Component
@PropertySource("classpath:server.properties")
public class SupervisorQuerier {

    private SupervisorQueries supervisorQueries;

    @Autowired
    public SupervisorQuerier(Environment environment) {
        Environment env = environment;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(env.getProperty("supervisor.api"))
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().findAndRegisterModules()))
                .build();
        supervisorQueries = retrofit.create(SupervisorQueries.class);

    }

    public SupervisorQueries getSupervisorQueries() {
        return supervisorQueries;
    }
}
