package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalTime;

@Entity
public class WorkTimePeriod extends AbstractEntity<Long> {
    private static final long serialVersionUID = 2140610419978157701L;

    @Column
    private LocalTime start;

    @Column
    private LocalTime end;

    @OneToOne(mappedBy = "workTimePeriod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TrackingData trackingData = new TrackingData();

    @ManyToOne
    private WorkDay workDay;

    public WorkTimePeriod() {
    }

    public WorkTimePeriod(LocalTime start, LocalTime end, TrackingData trackingData, WorkDay workDay) {
        this.start = start;
        this.end = end;
        this.trackingData = trackingData;
        this.workDay = workDay;
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

    public TrackingData getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(TrackingData trackingData) {
        this.trackingData = trackingData;
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
