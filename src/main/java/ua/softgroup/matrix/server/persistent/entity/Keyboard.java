package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Keyboard extends Metrics {
    private static final long serialVersionUID = 8580060699707698353L;

    @Column(columnDefinition = "TEXT")
    private String keyboardLog;

    public Keyboard() {
    }

    public Keyboard(String keyboardLog, Project project) {
        this.keyboardLog = keyboardLog.trim();
        setProject(project);
    }

    public String getKeyboardLog() {
        return keyboardLog;
    }

    public void setKeyboardLog(String keyboardLog) {
        this.keyboardLog = keyboardLog;
    }
}
