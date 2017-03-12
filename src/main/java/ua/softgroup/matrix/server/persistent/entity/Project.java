package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Integer rate;

    @Column
    private Integer rateCurrencyId;

    @Column
    private LocalDateTime workStarted;

    @Column
    private LocalDateTime checkpointTime;

    @Column
    private Integer todaySeconds = 0;

    @Column
    private Integer totalSeconds = 0;

    @Column
    private Integer idleSeconds = 0;

    @ManyToOne
    private User user;

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

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRateCurrencyId() {
        return rateCurrencyId;
    }

    public void setRateCurrencyId(Integer rateCurrencyId) {
        this.rateCurrencyId = rateCurrencyId;
    }

    public LocalDateTime getWorkStarted() {
        return workStarted;
    }

    public void setWorkStarted(LocalDateTime workStarted) {
        this.workStarted = workStarted;
    }

    public LocalDateTime getCheckpointTime() {
        return checkpointTime;
    }

    public void setCheckpointTime(LocalDateTime checkpointTime) {
        this.checkpointTime = checkpointTime;
    }

    public Integer getTodaySeconds() {
        return todaySeconds;
    }

    public void setTodaySeconds(Integer todayMinutes) {
        this.todaySeconds = todayMinutes;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalMinutes) {
        this.totalSeconds = totalMinutes;
    }

    public Integer getIdleSeconds() {
        return idleSeconds;
    }

    public void setIdleSeconds(Integer idleMinutes) {
        this.idleSeconds = idleMinutes;
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
                ", checkpointTime=" + checkpointTime +
                ", todaySeconds=" + todaySeconds +
                ", totalSeconds=" + totalSeconds +
                ", idleSeconds=" + idleSeconds +
                '}';
    }
}
