package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class WorkTime extends AbstractEntity<Long> {
    private static final long serialVersionUID = -7529514121101458195L;

    @Column
    private LocalDateTime startedWork;

    @Column
    private Long todayMinutes = 0L;

    @Column
    private Long totalMinutes = 0L;

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

    public WorkTime(Long id) {
        super.setId(id);
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

    public WorkTime(User user, Project project, Long todayMinutes) {
        this.user = user;
        this.project = project;
        this.todayMinutes = todayMinutes;
    }

    public WorkTime(Long todayMinutes, Long totalMinutes, Project project, User user) {
        this.todayMinutes = todayMinutes;
        this.totalMinutes = totalMinutes;
        this.project = project;
        this.user = user;
    }

    public LocalDateTime getStartedWork() {
        return startedWork;
    }

    public void setStartedWork(LocalDateTime startedWork) {
        this.startedWork = startedWork;
    }

    public Long getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(Long todayHours) {
        this.todayMinutes = todayHours;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Long totalMinutes) {
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
                "id=" + super.getId() +
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
