package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileJson {

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("monthly_rate")
    private Integer monthlyRate;
    @JsonProperty("monthly_rate_currency_id")
    private Integer monthlyRateCurrencyId;
    @JsonProperty("external_hourly_rate")
    private Integer externalHourlyRate;
    @JsonProperty("external_hourly_rate_currency_id")
    private Integer externalHourlyRateCurrencyId;
    @JsonProperty("internal_hourly_rate")
    private Integer internalHourlyRate;
    @JsonProperty("internal_hourly_rate_currency_id")
    private Integer internalHourlyRateCurrencyId;
    @JsonProperty("email_home")
    private String emailHome;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Integer getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Integer monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Integer getMonthlyRateCurrencyId() {
        return monthlyRateCurrencyId;
    }

    public void setMonthlyRateCurrencyId(Integer monthlyRateCurrencyId) {
        this.monthlyRateCurrencyId = monthlyRateCurrencyId;
    }

    public Integer getExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(Integer externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

    public Integer getExternalHourlyRateCurrencyId() {
        return externalHourlyRateCurrencyId;
    }

    public void setExternalHourlyRateCurrencyId(Integer externalHourlyRateCurrencyId) {
        this.externalHourlyRateCurrencyId = externalHourlyRateCurrencyId;
    }

    public Integer getInternalHourlyRate() {
        return internalHourlyRate;
    }

    public void setInternalHourlyRate(Integer internalHourlyRate) {
        this.internalHourlyRate = internalHourlyRate;
    }

    public Integer getInternalHourlyRateCurrencyId() {
        return internalHourlyRateCurrencyId;
    }

    public void setInternalHourlyRateCurrencyId(Integer internalHourlyRateCurrencyId) {
        this.internalHourlyRateCurrencyId = internalHourlyRateCurrencyId;
    }

    public String getEmailHome() {
        return emailHome;
    }

    public void setEmailHome(String emailHome) {
        this.emailHome = emailHome;
    }

    @Override
    public String toString() {
        return "ProfileJson{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", monthlyRate=" + monthlyRate +
                ", monthlyRateCurrencyId=" + monthlyRateCurrencyId +
                ", externalHourlyRate=" + externalHourlyRate +
                ", externalHourlyRateCurrencyId=" + externalHourlyRateCurrencyId +
                ", internalHourlyRate=" + internalHourlyRate +
                ", internalHourlyRateCurrencyId=" + internalHourlyRateCurrencyId +
                ", emailHome='" + emailHome + '\'' +
                '}';
    }
}
