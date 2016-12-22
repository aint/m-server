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
    private Integer mouseMetric;

    @ElementCollection
    @CollectionTable(name = "tracking_links")
    private List<String> links;

    @ElementCollection
    @CollectionTable(name = "tracking_screenshots")
    private List<String> screenshots;

    @ElementCollection
    @CollectionTable(name = "tracking_window_time")
    @MapKeyColumn(name = "window_title", length = 500)
    @Column(name = "time_seconds")
    private Map<String, Long> windowTimeMap = new LinkedHashMap<>();

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

    public Integer getMouseMetric() {
        return mouseMetric;
    }

    public void setMouseMetric(Integer mouseMetric) {
        this.mouseMetric = mouseMetric;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public Map<String, Long> getWindowTimeMap() {
        return windowTimeMap;
    }

    public void setWindowTimeMap(Map<String, Long> windowTimeMap) {
        this.windowTimeMap = windowTimeMap;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }
}
