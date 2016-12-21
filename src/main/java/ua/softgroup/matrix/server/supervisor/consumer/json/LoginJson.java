package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginJson {

    @JsonProperty
    private Boolean success;
    @JsonProperty
    private String message;
    @JsonProperty("tracker-token")
    private String trackerToken;
    @JsonProperty
    private UserJson user;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrackerToken() {
        return trackerToken;
    }

    public void setTrackerToken(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    public UserJson getUser() {
        return user;
    }

    public void setUser(UserJson user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginJson{" +
                "success=" + success +
                ", trackerToken='" + trackerToken + '\'' +
                ", user=" + user +
                '}';
    }
}
