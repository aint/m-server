package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vadim on 24.10.2016.
 */
public class CurrentUserInfoResponseModel implements RetrofitModel {

    @SerializedName("user") private UserModel userModel;

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
