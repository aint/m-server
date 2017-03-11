package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ClientSettings extends AbstractEntity<Long> {
    private static final long serialVersionUID = -4678740217747959072L;

    @Column
    private Integer screenshotUpdateFrequentlyInMinutes;

    @Column
    private Integer startDowntimeAfterInMinutes;

    @Column
    private Integer reportEditablePeriodInDays;

    public ClientSettings() {
    }

    public ClientSettings(Integer screenshotUpdateFrequentlyInMinutes,
                          Integer startDowntimeAfterInMinutes,
                          Integer reportEditablePeriodInDays) {
        this.screenshotUpdateFrequentlyInMinutes = screenshotUpdateFrequentlyInMinutes;
        this.startDowntimeAfterInMinutes = startDowntimeAfterInMinutes;
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    public Integer getScreenshotUpdateFrequentlyInMinutes() {
        return screenshotUpdateFrequentlyInMinutes;
    }

    public void setScreenshotUpdateFrequentlyInMinutes(Integer screenshotUpdateFrequentlyInMinutes) {
        this.screenshotUpdateFrequentlyInMinutes = screenshotUpdateFrequentlyInMinutes;
    }

    public Integer getStartDowntimeAfterInMinutes() {
        return startDowntimeAfterInMinutes;
    }

    public void setStartDowntimeAfterInMinutes(Integer startDowntimeAfterInMinutes) {
        this.startDowntimeAfterInMinutes = startDowntimeAfterInMinutes;
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
                "id=" + super.getId() +
                ", screenshotUpdateFrequentlyInMinutes=" + screenshotUpdateFrequentlyInMinutes +
                ", startDowntimeAfterInMinutes=" + startDowntimeAfterInMinutes +
                ", reportEditablePeriodInDays=" + reportEditablePeriodInDays +
                '}';
    }
}
