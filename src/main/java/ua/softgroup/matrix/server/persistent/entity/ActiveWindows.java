package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class ActiveWindows extends Metrics {
    private static final long serialVersionUID = 5299063888582451183L;

    @ElementCollection
    @MapKeyColumn(name = "window")
    @Column(name = "time")
    @CollectionTable(name = "activewindow_time")
    private Map<String, Long> windowTimeMap = new LinkedHashMap<>();

    public ActiveWindows() {
    }

    public ActiveWindows(Map<String, Long> windowTimeMap, WorkTime workTime) {
        super.setWorkTime(workTime);
        this.windowTimeMap = windowTimeMap;
    }

    public Map<String, Long> getWindowTimeMap() {
        return windowTimeMap;
    }

    public void setWindowTimeMap(Map<String, Long> windowTimeMap) {
        this.windowTimeMap = windowTimeMap;
    }

    @Override
    public String toString() {
        return "ActiveWindows{" +
                "windowTimeMap=" + windowTimeMap +
                '}';
    }
}
