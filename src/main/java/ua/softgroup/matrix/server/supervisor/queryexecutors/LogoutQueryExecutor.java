package ua.softgroup.matrix.server.supervisor.queryexecutors;

import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.LogoutResponseModel;

import java.io.IOException;

/**
 * Created by Vadim on 24.10.2016.
 */
class LogoutQueryExecutor implements QueryExecutor {

    private String trackerToken;

    LogoutQueryExecutor(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    @Override
    public Response executeQuery() throws IOException {
        Call<LogoutResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().logout(trackerToken);
        return call.execute();
    }



}
