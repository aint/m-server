package ua.softgroup.matrix.server.supervisor.consumer.endpoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrenciesResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.CurrentUserInfoResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.LoginResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.LogoutResponseModel;
import ua.softgroup.matrix.server.supervisor.consumer.json.UserActiveProjectsResponseModel;

public interface SupervisorEndpoint {

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponseModel> login(@Field("login") String login, @Field("pass") String password);

    @POST("auth/logout")
    Call<LogoutResponseModel> logout(@Header("tracker-token") String trackerToken);

    @POST("user/get-current-user-info")
    Call<CurrentUserInfoResponseModel> getCurrentUserInfo(@Header("tracker-token") String trackerToken);

    @POST("project/get-user-active-projects")
    Call<UserActiveProjectsResponseModel> getUserActiveProjects(@Header("tracker-token") String trackerToken);

    @POST("currency/get-currencies")
    Call<CurrenciesResponseModel> getCurrencies(@Header("tracker-token") String trackerToken);
}
