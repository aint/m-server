package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class WorkTimePeriod implements Serializable {
    private static final long serialVersionUID = 2140610419978157701L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDateTime start;

    @Column
    private LocalDateTime end;

    @ManyToOne
    private WorkDay workDay;

    public WorkTimePeriod() {
    }

    public WorkTimePeriod(LocalDateTime start, LocalDateTime end, WorkDay workDay) {
        this.start = start;
        this.end = end;
        this.workDay = workDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }

    @Override
    public String toString() {
        return "WorkTimePeriod{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", workDay=" + workDay +
                '}';
    }
}
