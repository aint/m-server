package ua.softgroup.matrix.server.supervisor.queryexecutors;

import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.CurrentUserInfoResponseModel;

import java.io.IOException;

/**
 * Created by Vadim on 24.10.2016.
 */
class GetCurrentUserInfoExecutor implements QueryExecutor {

    private String trackerToken;

    GetCurrentUserInfoExecutor(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    @Override
    public Response executeQuery() throws IOException {
        Call<CurrentUserInfoResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().getCurrentUserInfo(trackerToken);
        return call.execute();
    }

}
