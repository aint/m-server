package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column
    private String trackerToken;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private Long externalHourlyRate;

    @Column
    private Long externalHourlyRateCurrencyId;

    @Column
    private Long internalHourlyRate;

    @Column
    private Long internalHourlyRateCurrencyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackerToken() {
        return trackerToken;
    }

    public void setTrackerToken(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(Long externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

    public Long getExternalHourlyRateCurrencyId() {
        return externalHourlyRateCurrencyId;
    }

    public void setExternalHourlyRateCurrencyId(Long externalHourlyRateCurrencyId) {
        this.externalHourlyRateCurrencyId = externalHourlyRateCurrencyId;
    }

    public Long getInternalHourlyRate() {
        return internalHourlyRate;
    }

    public void setInternalHourlyRate(Long internalHourlyRate) {
        this.internalHourlyRate = internalHourlyRate;
    }

    public Long getInternalHourlyRateCurrencyId() {
        return internalHourlyRateCurrencyId;
    }

    public void setInternalHourlyRateCurrencyId(Long internalHourlyRateCurrencyId) {
        this.internalHourlyRateCurrencyId = internalHourlyRateCurrencyId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", trackerToken='" + trackerToken + '\'' +
                ", username='" + username + '\'' +
                ", password=" + password +
                ", externalHourlyRate=" + externalHourlyRate +
                ", externalHourlyRateCurrencyId=" + externalHourlyRateCurrencyId +
                ", internalHourlyRate=" + internalHourlyRate +
                ", internalHourlyRateCurrencyId=" + internalHourlyRateCurrencyId +
                '}';
    }
}
