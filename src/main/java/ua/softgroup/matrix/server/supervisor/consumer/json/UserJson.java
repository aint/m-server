package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserJson {

    @JsonProperty
    private long id;
    @JsonProperty
    private String email;
    @JsonProperty
    private String username;
    @JsonProperty("tracker_token")
    private String trackerToken;
    @JsonProperty
    private ProfileJson profile;

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

    public ProfileJson getProfile() {
        return profile;
    }

    public void setProfile(ProfileJson profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserJson{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", trackerToken='" + trackerToken + '\'' +
                ", profile=" + profile +
                '}';
    }
}
