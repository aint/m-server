package ua.softgroup.matrix.server.supervisor.queryexecutors;

import retrofit2.Call;
import retrofit2.Response;
import ua.softgroup.matrix.server.supervisor.SupervisorQueriesSingleton;
import ua.softgroup.matrix.server.supervisor.models.CurrenciesResponseModel;

import java.io.IOException;

/**
 * Created by Vadim on 24.10.2016.
 */
class GetCurrenciesExecutor implements QueryExecutor {

    private String trackerToken;

    GetCurrenciesExecutor(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    @Override
    public Response executeQuery() throws IOException {
        Call<CurrenciesResponseModel> call = SupervisorQueriesSingleton.getInstance().getSupervisorQueries().getCurrencies(trackerToken);
        return call.execute();
    }

}
