package ua.softgroup.matrix.server.supervisor.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vadim on 24.10.2016.
 */
public class UserModel implements RetrofitModel {

    @JsonProperty("id") private long id;
    @JsonProperty("email") private String email;
    @JsonProperty("username") private String username;
    @JsonProperty("tracker_token") private String trackerToken;
    @JsonProperty("profile") private ProfileModel profileModel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTrackerToken() {
        return trackerToken;
    }

    public void setTrackerToken(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public void setProfileModel(ProfileModel profileModel) {
        this.profileModel = profileModel;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", trackerToken='" + trackerToken + '\'' +
                ", profileModel=" + profileModel +
                '}';
    }
}
