package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String trackerToken;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String middleName;

    @Column
    private int monthlyRate;

    @Column
    private int monthlyRateCurrencyId;

    @Column
    private int externalHourlyRate;

    @Column
    private int externalHourlyRateCurrencyId;

    @Column
    private int internalHourlyRate;

    @Column
    private int internalHourlyRateCurrencyId;

    @Column
    private String emailHome;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports = new HashSet<>();

    public User() {
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", trackerToken='" + trackerToken + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", monthlyRate=" + monthlyRate +
                ", monthlyRateCurrencyId=" + monthlyRateCurrencyId +
                ", externalHourlyRate=" + externalHourlyRate +
                ", externalHourlyRateCurrencyId=" + externalHourlyRateCurrencyId +
                ", internalHourlyRate=" + internalHourlyRate +
                ", internalHourlyRateCurrencyId=" + internalHourlyRateCurrencyId +
                ", emailHome='" + emailHome + '\'' +
                ", password='" + password +
                '}';
    }
}
