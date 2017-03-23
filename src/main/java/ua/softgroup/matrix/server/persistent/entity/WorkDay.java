package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class WorkDay extends AbstractEntity<Long> {
    private static final long serialVersionUID = -5318207364986821484L;

    @Column
    @CreationTimestamp
    private LocalDate date;

    @Column
    private Integer workSeconds = 0;

    @Column
    private Integer idleSeconds = 0;

    @Column(columnDefinition = "TEXT")
    private String reportText;

    @ManyToOne
    private User author;

    @Column
    private LocalDateTime reportUpdated;

    @Column
    private boolean checked;

    @Column
    private Long jailerId = 0L;

    @Column
    private Double coefficient = 1.0;

    @OneToOne(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Tracking tracking;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkTimePeriod> workTimePeriods = new HashSet<>();

    public WorkDay() {
    }

    public WorkDay(Long id) {
        setId(id);
    }


    public WorkDay(Integer workSeconds, Integer idleSeconds) {
        this.workSeconds = workSeconds;
        this.idleSeconds = idleSeconds;
    }

    public WorkDay(User author, Project project, LocalDate date) {
        this.author = author;
        this.project = project;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getWorkSeconds() {
        return workSeconds;
    }

    public void setWorkSeconds(Integer workTimeMinutes) {
        this.workSeconds = workTimeMinutes;
    }

    public Integer getIdleSeconds() {
        return idleSeconds;
    }

    public void setIdleSeconds(Integer idleTimeMinutes) {
        this.idleSeconds = idleTimeMinutes;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getReportUpdated() {
        return reportUpdated;
    }

    public void setReportUpdated(LocalDateTime reportUpdated) {
        this.reportUpdated = reportUpdated;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Long getJailerId() {
        return jailerId;
    }

    public void setJailerId(Long jailerId) {
        this.jailerId = jailerId;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project workTime) {
        this.project = workTime;
    }

    public Set<WorkTimePeriod> getWorkTimePeriods() {
        return workTimePeriods;
    }

    public void setWorkTimePeriods(Set<WorkTimePeriod> workTimePeriods) {
        this.workTimePeriods = workTimePeriods;
    }

    @Override
    public String toString() {
        return "WorkDay{" +
                "id=" + super.getId() +
                ", date=" + date +
                ", workMinutes=" + workSeconds +
                ", idleMinutes=" + idleSeconds +
                ", checked=" + checked +
                ", coefficient=" + coefficient +
                '}';
    }
}
