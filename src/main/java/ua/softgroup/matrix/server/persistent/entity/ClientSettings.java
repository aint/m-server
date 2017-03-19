package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ClientSettings extends AbstractEntity<Long> {
    private static final long serialVersionUID = -4678740217747959072L;

    @Column
    private Integer checkpointFrequentlyInSeconds;

    @Column
    private Integer screenshotFrequentlyInSeconds;

    @Column
    private Integer startIdleAfterSeconds;

    @Column
    private Integer reportEditablePeriodInDays;

    public ClientSettings() {
    }

    public ClientSettings(Integer checkpointFrequentlyInSeconds, Integer screenshotFrequentlyInSeconds,
                          Integer startIdleAfterSeconds, Integer reportEditablePeriodInDays) {
        this.checkpointFrequentlyInSeconds = checkpointFrequentlyInSeconds;
        this.screenshotFrequentlyInSeconds = screenshotFrequentlyInSeconds;
        this.startIdleAfterSeconds = startIdleAfterSeconds;
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    public Integer getCheckpointFrequentlyInSeconds() {
        return checkpointFrequentlyInSeconds;
    }

    public void setCheckpointFrequentlyInSeconds(Integer checkpointFrequentlyInSeconds) {
        this.checkpointFrequentlyInSeconds = checkpointFrequentlyInSeconds;
    }

    public Integer getScreenshotFrequentlyInSeconds() {
        return screenshotFrequentlyInSeconds;
    }

    public void setScreenshotFrequentlyInSeconds(Integer screenshotFrequentlyInSeconds) {
        this.screenshotFrequentlyInSeconds = screenshotFrequentlyInSeconds;
    }

    public Integer getStartIdleAfterSeconds() {
        return startIdleAfterSeconds;
    }

    public void setStartIdleAfterSeconds(Integer startIdleAfterSeconds) {
        this.startIdleAfterSeconds = startIdleAfterSeconds;
    }

    public Integer getReportEditablePeriodInDays() {
        return reportEditablePeriodInDays;
    }

    public void setReportEditablePeriodInDays(Integer reportEditablePeriodInDays) {
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    @Override
    public String toString() {
        return "ClientSettings{" +
                "checkpointFrequentlyInSeconds=" + checkpointFrequentlyInSeconds +
                ", screenshotFrequentlyInSeconds=" + screenshotFrequentlyInSeconds +
                ", startIdleAfterSeconds=" + startIdleAfterSeconds +
                ", reportEditablePeriodInDays=" + reportEditablePeriodInDays +
                '}';
    }
}
