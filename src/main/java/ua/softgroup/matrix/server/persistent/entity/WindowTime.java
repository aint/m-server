package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class WindowTime extends AbstractEntity<Long> {
    private static final long serialVersionUID = -3216454660635129210L;

    @Column(columnDefinition = "TEXT")
    private String windowTitle;

    @Column
    private LocalTime startTime;

    @Column
    private Integer time = 0;

    @ManyToOne
    private TrackingData trackingData;

    public WindowTime() {
    }

    public WindowTime(String windowTitle, LocalTime startTime, Integer time, TrackingData trackingData) {
        this.windowTitle = windowTitle;
        this.startTime = startTime;
        this.time = time;
        this.trackingData = trackingData;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public TrackingData getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(TrackingData trackingData) {
        this.trackingData = trackingData;
    }
}
