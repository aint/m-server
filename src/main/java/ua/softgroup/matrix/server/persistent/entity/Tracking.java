package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class Tracking extends AbstractEntity<Long> {
    private static final long serialVersionUID = -6554909949520971201L;

    @Column
    private String keyboardText;

    @Column
    private Integer mouseFootage;

    @ElementCollection
    @CollectionTable(name = "tracking_screenshots")
    private List<String> screenshots;

    @ElementCollection
    @CollectionTable(name = "tracking_window_time")
    @MapKeyColumn(name = "window_title")
    @Column(name = "time_seconds")
    private Map<String, Integer> windowTimeMap = new LinkedHashMap<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_day_id")
    private WorkDay workDay;

    public Tracking() {
    }

    public Tracking(WorkDay workDay) {
        this.workDay = workDay;
    }

    public String getKeyboardText() {
        return keyboardText;
    }

    public void setKeyboardText(String keyboardText) {
        this.keyboardText = keyboardText;
    }

    public Integer getMouseFootage() {
        return mouseFootage;
    }

    public void setMouseFootage(Integer mouseFootage) {
        this.mouseFootage = mouseFootage;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
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
