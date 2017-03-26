package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class TrackingData extends AbstractEntity<Long> {
    private static final long serialVersionUID = -6554909949520971201L;

    @Column(columnDefinition = "LONGTEXT")
    private String keyboardText = "";

    @Column
    private Double mouseFootage = 0.0;

    @OneToMany(mappedBy = "trackingData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Screenshot> screenshots = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "tracking_window_time")
    @MapKeyColumn(name = "window_title")
    @Column(name = "time_seconds")
    private Map<String, Integer> windowTimeMap = new LinkedHashMap<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_day_id")
    private WorkDay workDay;

    public TrackingData() {
    }

    public TrackingData(WorkDay workDay) {
        this.workDay = workDay;
    }

    public String getKeyboardText() {
        return keyboardText;
    }

    public void setKeyboardText(String keyboardText) {
        this.keyboardText = keyboardText;
    }

    public Double getMouseFootage() {
        return mouseFootage;
    }

    public void setMouseFootage(Double mouseFootage) {
        this.mouseFootage = mouseFootage;
    }

    public Set<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(Set<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public Map<String, Integer> getWindowTimeMap() {
        return windowTimeMap;
    }

    public void setWindowTimeMap(Map<String, Integer> windowTimeMap) {
        this.windowTimeMap = windowTimeMap;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }
}
