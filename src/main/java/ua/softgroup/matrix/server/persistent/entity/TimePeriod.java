package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class TimePeriod implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime start;

    @Column
    private LocalDateTime end;

    @Column
    private boolean externalHourlyRate = false;

    @ManyToOne
    private WorkTime workTime;

    public TimePeriod() {
    }

    public TimePeriod(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public TimePeriod(LocalDateTime start, LocalDateTime end, WorkTime workTime) {
        this.start = start;
        this.end = end;
        this.workTime = workTime;
    }

    public TimePeriod(LocalDateTime start, LocalDateTime end, boolean externalHourlyRate, WorkTime workTime) {
        this.start = start;
        this.end = end;
        this.externalHourlyRate = externalHourlyRate;
        this.workTime = workTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public boolean isExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(boolean externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }
}
