package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class WorkTime implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime startedWork;

    @Column
    private Integer todayMinutes = 0;

    @Column
    private Integer totalMinutes = 0;

    @Column
    private Long downtimeMinutes = 0L;

    @Column
    private LocalDateTime startDowntime;

    @Column
    private Integer rate = 0;

    @Column
    private Integer rateCurrencyId = 0;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkDay> workDays;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyboard> keyboardLogs;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshots;

    public WorkTime() {
    }

    public WorkTime(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    public WorkTime(LocalDateTime startedWork, Project project, User user) {
        this.startedWork = startedWork;
        this.project = project;
        this.user = user;
    }

    public WorkTime(User user, Project project, Integer todayMinutes) {
        this.user = user;
        this.project = project;
        this.todayMinutes = todayMinutes;
    }

    public WorkTime(Integer todayMinutes, Integer totalMinutes, Project project, User user) {
        this.todayMinutes = todayMinutes;
        this.totalMinutes = totalMinutes;
        this.project = project;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartedWork() {
        return startedWork;
    }

    public void setStartedWork(LocalDateTime startedWork) {
        this.startedWork = startedWork;
    }

    public Integer getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(Integer todayHours) {
        this.todayMinutes = todayHours;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Long getDowntimeMinutes() {
        return downtimeMinutes;
    }

    public void setDowntimeMinutes(Long downtimeMinutes) {
        this.downtimeMinutes = downtimeMinutes;
    }

    public LocalDateTime getStartDowntime() {
        return startDowntime;
    }

    public void setStartDowntime(LocalDateTime startTime) {
        this.startDowntime = startTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public List<Keyboard> getKeyboardLogs() {
        return keyboardLogs;
    }

    public void setKeyboardLogs(List<Keyboard> keyboardLogs) {
        this.keyboardLogs = keyboardLogs;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public Integer getRateCurrencyId() {
        return rateCurrencyId;
    }

    public void setRateCurrencyId(Integer rateCurrencyId) {
        this.rateCurrencyId = rateCurrencyId;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "id=" + id +
                ", startedWork=" + startedWork +
                ", todayMinutes=" + todayMinutes +
                ", totalMinutes=" + totalMinutes +
                ", downtimeMinutes=" + downtimeMinutes +
                ", startDowntime=" + startDowntime +
                ", rate=" + rate +
                ", rateCurrencyId=" + rateCurrencyId +
                '}';
    }
}
