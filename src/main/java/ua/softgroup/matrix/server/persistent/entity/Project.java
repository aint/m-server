package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project extends AbstractEntity<Long>  {
    private static final long serialVersionUID = 1L;

    @Column
    private Long supervisorId;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column
    private String description;

    @Column
    private String authorName;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Long rate;

    @Column
    private Long rateCurrencyId;

    @Column
    private LocalDateTime workStarted;

    @Column
    private LocalDateTime idleStarted;

    @Column
    private Long todayMinutes = 0L;

    @Column
    private Long totalMinutes = 0L;

    @Column
    private Long idleMinutes = 0L;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkDay> workDays;

    public Project() {
    }

    public Project(Long id) {
        super.setId(id);
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }

    public Long getRateCurrencyId() {
        return rateCurrencyId;
    }

    public void setRateCurrencyId(Long rateCurrencyId) {
        this.rateCurrencyId = rateCurrencyId;
    }

    public LocalDateTime getWorkStarted() {
        return workStarted;
    }

    public void setWorkStarted(LocalDateTime workStarted) {
        this.workStarted = workStarted;
    }

    public LocalDateTime getIdleStarted() {
        return idleStarted;
    }

    public void setIdleStarted(LocalDateTime idleStarted) {
        this.idleStarted = idleStarted;
    }

    public Long getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(Long todayMinutes) {
        this.todayMinutes = todayMinutes;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Long getIdleMinutes() {
        return idleMinutes;
    }

    public void setIdleMinutes(Long idleMinutes) {
        this.idleMinutes = idleMinutes;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<WorkDay> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<WorkDay> workDays) {
        this.workDays = workDays;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + super.getId() +
                ", supervisorId=" + supervisorId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", authorName='" + authorName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rate=" + rate +
                ", rateCurrencyId=" + rateCurrencyId +
                ", workStarted=" + workStarted +
                ", idleStarted=" + idleStarted +
                ", todayMinutes=" + todayMinutes +
                ", totalMinutes=" + totalMinutes +
                ", idleMinutes=" + idleMinutes +
                '}';
    }
}
