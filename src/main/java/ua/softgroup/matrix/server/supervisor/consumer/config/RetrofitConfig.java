package ua.softgroup.matrix.server.supervisor.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Configuration
@PropertySource("classpath:server.properties")
public class RetrofitConfig {

    @Bean
    public SupervisorEndpoint supervisorEndpoint(Environment environment) {
        return new Retrofit.Builder()
                .baseUrl(environment.getProperty("supervisor.api"))
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().findAndRegisterModules()))
                .build()
                .create(SupervisorEndpoint.class);
    }

}
