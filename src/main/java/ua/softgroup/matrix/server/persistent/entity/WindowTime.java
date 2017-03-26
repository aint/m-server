package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class WindowTime extends AbstractEntity<Long> {
    private static final long serialVersionUID = -3216454660635129210L;

    @Column(columnDefinition = "TEXT")
    private String windowTitle;

    @Column
    private Integer time = 0;

    @ManyToOne
    private TrackingData trackingData;

    public WindowTime() {
    }

    public WindowTime(String windowTitle, Integer time, TrackingData trackingData) {
        this.windowTitle = windowTitle;
        this.time = time;
        this.trackingData = trackingData;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
