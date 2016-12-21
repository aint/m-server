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
    private Long monthlyRate;
    @JsonProperty("monthly_rate_currency_id")
    private Long monthlyRateCurrencyId;
    @JsonProperty("external_hourly_rate")
    private Long externalHourlyRate;
    @JsonProperty("external_hourly_rate_currency_id")
    private Long externalHourlyRateCurrencyId;
    @JsonProperty("internal_hourly_rate")
    private Long internalHourlyRate;
    @JsonProperty("internal_hourly_rate_currency_id")
    private Long internalHourlyRateCurrencyId;
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

    public Long getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Long monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Long getMonthlyRateCurrencyId() {
        return monthlyRateCurrencyId;
    }

    public void setMonthlyRateCurrencyId(Long monthlyRateCurrencyId) {
        this.monthlyRateCurrencyId = monthlyRateCurrencyId;
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
