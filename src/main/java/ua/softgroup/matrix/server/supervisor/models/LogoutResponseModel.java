package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vadim on 24.10.2016.
 */
public class LogoutResponseModel implements RetrofitModel {

    @SerializedName("success") private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "LogoutResponseModel{" +
                "success=" + success +
                '}';
    }
}
