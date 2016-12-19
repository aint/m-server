package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class WorkTimePeriod extends AbstractEntity<Long> {
    private static final long serialVersionUID = 2140610419978157701L;

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
                "id=" + super.getId() +
                ", start=" + start +
                ", end=" + end +
                ", workDay=" + workDay +
                '}';
    }
}
