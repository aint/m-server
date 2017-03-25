package ua.softgroup.matrix.server.supervisor.consumer.endpoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ua.softgroup.matrix.server.supervisor.consumer.json.ActiveProjectsJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrenciesJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.LoginJson;
import ua.softgroup.matrix.server.supervisor.consumer.json.SettingsJson;

public interface SupervisorEndpoint {

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginJson> login(@Field("login") String login, @Field("pass") String password);

    @POST("project/get-user-active-projects")
    Call<ActiveProjectsJson> getUserActiveProjects(@Header("tracker-token") String trackerToken);

    @POST("currency/get-currencies")
    Call<CurrenciesJson> getCurrencies(@Header("tracker-token") String trackerToken);

    @POST("setting/get-tracker-settings")
    Call<SettingsJson> getTrackerSettings(@Header("tracker-token") String trackerToken);
}
