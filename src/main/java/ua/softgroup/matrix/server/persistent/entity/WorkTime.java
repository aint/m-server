package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimePeriod> timePeriods;

    @OneToOne(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Downtime downtime;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyboard> keyboardLogs;

    @OneToMany(mappedBy = "workTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshots;

    public WorkTime() {
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

    public Set<TimePeriod> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods(Set<TimePeriod> timePeriods) {
        this.timePeriods = timePeriods;
    }

    public Downtime getDowntime() {
        return downtime;
    }

    public void setDowntime(Downtime downtime) {
        this.downtime = downtime;
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

    @Override
    public String toString() {
        return "WorkTime{" +
                "id=" + id +
                ", startedWork=" + startedWork +
                ", todayMinutes=" + todayMinutes +
                ", totalMinutes=" + totalMinutes +
                '}';
    }
}
