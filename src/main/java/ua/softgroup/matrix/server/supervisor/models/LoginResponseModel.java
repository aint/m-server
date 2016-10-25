package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vadim on 24.10.2016.
 */
public class LoginResponseModel implements RetrofitModel {

    @SerializedName("success") private Boolean success;
    @SerializedName("tracker-token") private String trackerToken;
    @SerializedName("user") private UserModel userModel;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTrackerToken() {
        return trackerToken;
    }

    public void setTrackerToken(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public String toString() {
        return "LoginResponseModel{" +
                "success=" + success +
                ", trackerToken='" + trackerToken + '\'' +
                ", userModel=" + userModel +
                '}';
    }
}
