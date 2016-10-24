package ua.softgroup.matrix.server.model;

import java.io.Serializable;

public class ClientSettingsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int version;

    private int screenshotUpdateFrequently;

    private int keyboardUpdateFrequently;

    public ClientSettingsModel() {
    }

    public ClientSettingsModel(int screenshotUpdateFrequently, int keyboardUpdateFrequently) {
        this.screenshotUpdateFrequently = screenshotUpdateFrequently;
        this.keyboardUpdateFrequently = keyboardUpdateFrequently;
    }

    public ClientSettingsModel(int version, int screenshotUpdateFrequently, int keyboardUpdateFrequently) {
        this.version = version;
        this.screenshotUpdateFrequently = screenshotUpdateFrequently;
        this.keyboardUpdateFrequently = keyboardUpdateFrequently;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getScreenshotUpdateFrequently() {
        return screenshotUpdateFrequently;
    }

    public void setScreenshotUpdateFrequently(int screenshotUpdateFrequently) {
        this.screenshotUpdateFrequently = screenshotUpdateFrequently;
    }

    public int getKeyboardUpdateFrequently() {
        return keyboardUpdateFrequently;
    }

    public void setKeyboardUpdateFrequently(int keyboardUpdateFrequently) {
        this.keyboardUpdateFrequently = keyboardUpdateFrequently;
    }
}
