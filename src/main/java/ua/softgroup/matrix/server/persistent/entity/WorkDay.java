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
    private Long workMinutes = 0L;

    @Column
    private Long idleMinutes = 0L;

    @Column
    private boolean checked = false;

    @Column
    private Double coefficient = 1.0;

    @OneToOne(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Report report;

    @OneToOne(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Tracking tracking;

    @ManyToOne
    private User checker;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkTimePeriod> workTimePeriods;

    public WorkDay() {
    }

    public WorkDay(Long workMinutes, Long idleMinutes) {
        this.workMinutes = workMinutes;
        this.idleMinutes = idleMinutes;
    }

    public WorkDay(Long workMinutes, Long idleMinutes, Project project) {
        this.workMinutes = workMinutes;
        this.idleMinutes = idleMinutes;
        this.project = project;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getWorkMinutes() {
        return workMinutes;
    }

    public void setWorkMinutes(Long workTimeMinutes) {
        this.workMinutes = workTimeMinutes;
    }

    public Long getIdleMinutes() {
        return idleMinutes;
    }

    public void setIdleMinutes(Long idleTimeMinutes) {
        this.idleMinutes = idleTimeMinutes;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public User getChecker() {
        return checker;
    }

    public void setChecker(User checker) {
        this.checker = checker;
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
                ", workMinutes=" + workMinutes +
                ", idleMinutes=" + idleMinutes +
                ", checked=" + checked +
                ", coefficient=" + coefficient +
                '}';
    }
}
