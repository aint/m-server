package ua.softgroup.matrix.server.supervisor.producer.json.tracking;

import java.time.LocalTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TrackingPeriodJson {

    private LocalTime start;
    private LocalTime end;
    private String windowName;
    private String keyLogger;
    private String screenshot;

    public TrackingPeriodJson() {
    }

    public TrackingPeriodJson(LocalTime start, LocalTime end, String keyLogger, String[] screenshotAndTitle) {
        this.start = start;
        this.end = end;
        this.keyLogger = keyLogger;
        this.screenshot = screenshotAndTitle[0];
        this.windowName = screenshotAndTitle[1];
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public String getKeyLogger() {
        return keyLogger;
    }

    public void setKeyLogger(String keyLogger) {
        this.keyLogger = keyLogger;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }
}
