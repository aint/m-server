package ua.softgroup.matrix.server.supervisor;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.server.supervisor.endpoint.SupervisorQueries;

public class SupervisorQueriesSingleton {

    private static final String BASE_URL = "http://test2.core.softgroup.ua/backend/web/api/";

    private static SupervisorQueriesSingleton instance;
    private SupervisorQueries supervisorQueries;

    private SupervisorQueriesSingleton() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().findAndRegisterModules()))
                .build();
        supervisorQueries = retrofit.create(SupervisorQueries.class);

    }

    public static synchronized SupervisorQueriesSingleton getInstance(){
        if (instance == null) {
            instance = new SupervisorQueriesSingleton();
        }
        return instance;
    }

    public SupervisorQueries getSupervisorQueries() {
        return supervisorQueries;
    }
}
