package ua.softgroup.matrix.server.supervisor.endpoint;

import retrofit2.Call;
import retrofit2.http.*;
import ua.softgroup.matrix.server.supervisor.models.*;

import java.util.Map;

public interface SupervisorQueries {

    @FormUrlEncoded
    @POST("auth/login")
//    Call<LoginResponseModel> login(@FieldMap Map<String,String> map);
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
