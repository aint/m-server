package ua.softgroup.matrix.server.supervisor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.softgroup.matrix.server.supervisor.endpoint.SupervisorQueries;

public class SupervisorQueriesSingleton {

    private static final String BASE_URL = "http://test.core.softgroup.ua/backend/web/api/";

    private static SupervisorQueriesSingleton instance;
    private SupervisorQueries supervisorQueries;

    private SupervisorQueriesSingleton() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
