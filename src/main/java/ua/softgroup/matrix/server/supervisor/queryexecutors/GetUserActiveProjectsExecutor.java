package ua.softgroup.matrix.server.supervisor.queryexecutors;

import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.UserActiveProjectsResponseModel;

import java.io.IOException;

/**
 * Created by Vadim on 24.10.2016.
 */
class GetUserActiveProjectsExecutor implements QueryExecutor {

    private String trackerToken;

    GetUserActiveProjectsExecutor(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    @Override
    public Response executeQuery() throws IOException {
        Call<UserActiveProjectsResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().getUserActiveProjects(trackerToken);
        return call.execute();
    }

}
