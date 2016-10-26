package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="WorkTime")
public class WorkTime implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime startedWork;

    @Column
    private Double todayHours = 0D;

    @Column
    private Integer totalMinutes = 0;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    public WorkTime() {
    }

    public WorkTime(LocalDateTime startedWork, Project project, User user) {
        this.startedWork = startedWork;
        this.project = project;
        this.user = user;
    }

    public WorkTime(User user, Project project, Double todayHours) {
        this.user = user;
        this.project = project;
        this.todayHours = todayHours;
    }

    public WorkTime(Double todayHours, Integer totalMinutes, Project project, User user) {
        this.todayHours = todayHours;
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

    public Double getTodayHours() {
        return todayHours;
    }

    public void setTodayHours(Double todayHours) {
        this.todayHours = todayHours;
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

}
