package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class WorkDay implements Serializable {
    private static final long serialVersionUID = -5318207364986821484L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne
    private User checker;

    @ManyToOne
    private WorkTime workTime;

    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkTimePeriod> workTimePeriods;

    public WorkDay() {
    }

    public WorkDay(Long workMinutes, Long idleMinutes) {
        this.workMinutes = workMinutes;
        this.idleMinutes = idleMinutes;
    }

    public WorkDay(Long workMinutes, Long idleMinutes, WorkTime workTime) {
        this.workMinutes = workMinutes;
        this.idleMinutes = idleMinutes;
        this.workTime = workTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getChecker() {
        return checker;
    }

    public void setChecker(User checker) {
        this.checker = checker;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
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
                "id=" + id +
                ", date=" + date +
                ", workMinutes=" + workMinutes +
                ", idleMinutes=" + idleMinutes +
                ", checked=" + checked +
                ", coefficient=" + coefficient +
                '}';
    }
}
