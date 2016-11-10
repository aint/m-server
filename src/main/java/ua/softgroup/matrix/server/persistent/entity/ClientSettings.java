package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ClientSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int settingsVersion;

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

    public ClientSettings(int settingsVersion,
                          int screenshotUpdateFrequentlyInMinutes,
                          int keyboardUpdateFrequentlyInMinutes,
                          int startDowntimeAfterInMinutes,
                          int reportEditablePeriodInDays) {
        this.settingsVersion = settingsVersion;
        this.screenshotUpdateFrequentlyInMinutes = screenshotUpdateFrequentlyInMinutes;
        this.keyboardUpdateFrequentlyInMinutes = keyboardUpdateFrequentlyInMinutes;
        this.startDowntimeAfterInMinutes = startDowntimeAfterInMinutes;
        this.reportEditablePeriodInDays = reportEditablePeriodInDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getSettingsVersion() {
        return settingsVersion;
    }

    public void setSettingsVersion(int settingsVersion) {
        this.settingsVersion = settingsVersion;
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
                "id=" + id +
                ", settingsVersion=" + settingsVersion +
                ", screenshotUpdateFrequentlyInMinutes=" + screenshotUpdateFrequentlyInMinutes +
                ", keyboardUpdateFrequentlyInMinutes=" + keyboardUpdateFrequentlyInMinutes +
                ", startDowntimeAfterInMinutes=" + startDowntimeAfterInMinutes +
                ", reportEditablePeriodInDays=" + reportEditablePeriodInDays +
                '}';
    }
}
