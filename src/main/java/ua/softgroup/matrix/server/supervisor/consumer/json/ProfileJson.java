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
    private int monthlyRate;
    @JsonProperty("monthly_rate_currency_id")
    private int monthlyRateCurrencyId;
    @JsonProperty("external_hourly_rate")
    private int externalHourlyRate;
    @JsonProperty("external_hourly_rate_currency_id")
    private int externalHourlyRateCurrencyId;
    @JsonProperty("internal_hourly_rate")
    private int internalHourlyRate;
    @JsonProperty("internal_hourly_rate_currency_id")
    private int internalHourlyRateCurrencyId;
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

    public int getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(int monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public int getMonthlyRateCurrencyId() {
        return monthlyRateCurrencyId;
    }

    public void setMonthlyRateCurrencyId(int monthlyRateCurrencyId) {
        this.monthlyRateCurrencyId = monthlyRateCurrencyId;
    }

    public int getExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(int externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

    public int getExternalHourlyRateCurrencyId() {
        return externalHourlyRateCurrencyId;
    }

    public void setExternalHourlyRateCurrencyId(int externalHourlyRateCurrencyId) {
        this.externalHourlyRateCurrencyId = externalHourlyRateCurrencyId;
    }

    public int getInternalHourlyRate() {
        return internalHourlyRate;
    }

    public void setInternalHourlyRate(int internalHourlyRate) {
        this.internalHourlyRate = internalHourlyRate;
    }

    public int getInternalHourlyRateCurrencyId() {
        return internalHourlyRateCurrencyId;
    }

    public void setInternalHourlyRateCurrencyId(int internalHourlyRateCurrencyId) {
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
