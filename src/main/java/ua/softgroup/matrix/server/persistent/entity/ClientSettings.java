package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ClientSettings extends AbstractEntity<Long> {
    private static final long serialVersionUID = -4678740217747959072L;

    @Column
    private int screenshotUpdateFrequentlyInMinutes;

    @Column
    private int keyboardUpdateFrequentlyInMinutes;

    @Column
    private int startDowntimeAfterInMinutes;

    @Column
    private int reportEditablePeriodInDays;

    public ClientSettings() {
    }

    public ClientSettings(int screenshotUpdateFrequentlyInMinutes,
                          int keyboardUpdateFrequentlyInMinutes,
                          int startDowntimeAfterInMinutes,
                          int reportEditablePeriodInDays) {
        this.screenshotUpdateFrequentlyInMinutes = screenshotUpdateFrequentlyInMinutes;
        this.keyboardUpdateFrequentlyInMinutes = keyboardUpdateFrequentlyInMinutes;
        this.startDowntimeAfterInMinutes = startDowntimeAfterInMinutes;
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    public int getScreenshotUpdateFrequentlyInMinutes() {
        return screenshotUpdateFrequentlyInMinutes;
    }

    public void setScreenshotUpdateFrequentlyInMinutes(int screenshotUpdateFrequentlyInMinutes) {
        this.screenshotUpdateFrequentlyInMinutes = screenshotUpdateFrequentlyInMinutes;
    }

    public int getKeyboardUpdateFrequentlyInMinutes() {
        return keyboardUpdateFrequentlyInMinutes;
    }

    public void setKeyboardUpdateFrequentlyInMinutes(int keyboardUpdateFrequentlyInMinutes) {
        this.keyboardUpdateFrequentlyInMinutes = keyboardUpdateFrequentlyInMinutes;
    }

    public int getStartDowntimeAfterInMinutes() {
        return startDowntimeAfterInMinutes;
    }

    public void setStartDowntimeAfterInMinutes(int startDowntimeAfterInMinutes) {
        this.startDowntimeAfterInMinutes = startDowntimeAfterInMinutes;
    }

    public int getReportEditablePeriodInDays() {
        return reportEditablePeriodInDays;
    }

    public void setReportEditablePeriodInDays(int reportEditablePeriodInDays) {
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    @Override
    public String toString() {
        return "ClientSettings{" +
                "id=" + super.getId() +
                ", screenshotUpdateFrequentlyInMinutes=" + screenshotUpdateFrequentlyInMinutes +
                ", keyboardUpdateFrequentlyInMinutes=" + keyboardUpdateFrequentlyInMinutes +
                ", startDowntimeAfterInMinutes=" + startDowntimeAfterInMinutes +
                ", reportEditablePeriodInDays=" + reportEditablePeriodInDays +
                '}';
    }
}
