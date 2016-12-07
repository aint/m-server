package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vadim on 24.10.2016.
 */
public class CurrentUserInfoResponseModel implements RetrofitModel {

    @JsonProperty("user") private UserModel userModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public String toString() {
        return "CurrentUserInfoResponseModel{" +
                "userModel=" + userModel +
                '}';
    }
}
