package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Downtime implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long minutes = 0;

    @Column
    private LocalDateTime startTime;

    @OneToOne
    private WorkTime workTime;

    public Downtime() {
    }

    public Downtime(WorkTime workTime) {
        this.workTime = workTime;
    }

    public Downtime(long minutes, LocalDateTime startTime, WorkTime workTime) {
        this.minutes = minutes;
        this.startTime = startTime;
        this.workTime = workTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }
}
