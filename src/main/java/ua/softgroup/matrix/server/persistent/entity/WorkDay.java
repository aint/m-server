package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    private Long workTimeMinutes = 0L;

    @Column
    private Long idleTimeMinutes = 0L;

    @ManyToOne
    private WorkTime workTime;

    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorktimePeriod> workTimePeriods;

    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DowntimePeriod> idleTimePeriods;

    public WorkDay() {
    }

    public WorkDay(Long workTimeMinutes, Long idleTimeMinutes) {
        this.workTimeMinutes = workTimeMinutes;
        this.idleTimeMinutes = idleTimeMinutes;
    }

    public WorkDay(Long workTimeMinutes, Long idleTimeMinutes, WorkTime workTime) {
        this.workTimeMinutes = workTimeMinutes;
        this.idleTimeMinutes = idleTimeMinutes;
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

    public Long getWorkTimeMinutes() {
        return workTimeMinutes;
    }

    public void setWorkTimeMinutes(Long workTimeMinutes) {
        this.workTimeMinutes = workTimeMinutes;
    }

    public Long getIdleTimeMinutes() {
        return idleTimeMinutes;
    }

    public void setIdleTimeMinutes(Long idleTimeMinutes) {
        this.idleTimeMinutes = idleTimeMinutes;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }

    public Set<WorktimePeriod> getWorkTimePeriods() {
        return workTimePeriods;
    }

    public void setWorkTimePeriods(Set<WorktimePeriod> workTimePeriods) {
        this.workTimePeriods = workTimePeriods;
    }

    public Set<DowntimePeriod> getIdleTimePeriods() {
        return idleTimePeriods;
    }

    public void setIdleTimePeriods(Set<DowntimePeriod> idleTimePeriods) {
        this.idleTimePeriods = idleTimePeriods;
    }

    @Override
    public String toString() {
        return "WorkDay{" +
                "id=" + id +
                ", date=" + date +
                ", workTimeMinutes=" + workTimeMinutes +
                ", idleTimeMinutes=" + idleTimeMinutes +
                '}';
    }
}
