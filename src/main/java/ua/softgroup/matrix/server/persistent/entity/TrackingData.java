package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy = "trackingData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WindowTime> activeWindows = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_period_id")
    private WorkTimePeriod workTimePeriod;

    public TrackingData() {
    }

    public TrackingData(WorkTimePeriod workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
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

    public List<WindowTime> getActiveWindows() {
        return activeWindows;
    }

    public void setActiveWindows(List<WindowTime> activeWindows) {
        this.activeWindows = activeWindows;
    }

    public WorkTimePeriod getWorkTimePeriod() {
        return workTimePeriod;
    }

    public void setWorkTimePeriod(WorkTimePeriod workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
    }
}
