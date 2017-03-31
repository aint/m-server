package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

@Entity
public class WorkTimePeriod extends AbstractEntity<Long> {
    private static final long serialVersionUID = 2140610419978157701L;

    @Column
    private LocalTime start;

    @Column
    private LocalTime end;

    @ManyToOne
    private WorkDay workDay;

    public WorkTimePeriod() {
    }

    public WorkTimePeriod(LocalTime start, LocalTime end, WorkDay workDay) {
        this.start = start;
        this.end = end;
        this.workDay = workDay;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
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
