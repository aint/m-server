package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ClientSettings1")
public class ClientSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int settingsVersion;

    @Column
    private int screenshotUpdateFrequently;

    @Column
    private int keyboardUpdateFrequently;

    public ClientSettings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientSettings(int screenshotUpdateFrequently, int keyboardUpdateFrequently) {
        this.screenshotUpdateFrequently = screenshotUpdateFrequently;
        this.keyboardUpdateFrequently = keyboardUpdateFrequently;
    }

    public int getSettingsVersion() {
        return settingsVersion;
    }

    public void setSettingsVersion(int settingsVersion) {
        this.settingsVersion = settingsVersion;
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
