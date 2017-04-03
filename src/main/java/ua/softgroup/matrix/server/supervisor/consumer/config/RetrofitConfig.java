package ua.softgroup.matrix.server.supervisor.consumer.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Configuration
public class RetrofitConfig {

    @Bean
    public SupervisorEndpoint supervisorEndpoint(Environment environment, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl(environment.getRequiredProperty("supervisor.api"))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper.findAndRegisterModules()))
                .build()
                .create(SupervisorEndpoint.class);
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .createXmlMapper(false)
                .build()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
    }

}
