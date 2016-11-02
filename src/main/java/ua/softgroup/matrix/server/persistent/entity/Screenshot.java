package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class Screenshot extends Metrics {
    private static final long serialVersionUID = -5537683353413610686L;

    @Column(columnDefinition = "TEXT")
    private String screenshotLink;

    public Screenshot() {
    }

    public Screenshot(String screenshotLink, WorkTime workTime) {
        this.screenshotLink = screenshotLink;
        setWorkTime(workTime);
    }

    public String getScreenshotLink() {
        return screenshotLink;
    }

    public void setScreenshotLink(String screenshotLink) {
        this.screenshotLink = screenshotLink;
    }
}
