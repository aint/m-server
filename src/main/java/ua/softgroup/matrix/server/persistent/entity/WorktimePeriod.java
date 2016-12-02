package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class WorktimePeriod extends AbstractPeriod {
    private static final long serialVersionUID = 2140610419978157701L;

    @Column
    private boolean externalHourlyRate = false;

    public WorktimePeriod() {
    }

    public WorktimePeriod(LocalDateTime start, LocalDateTime end, WorkDay workDay) {
        setStart(start);
        setEnd(end);
        setWorkDay(workDay);
    }

    public WorktimePeriod(LocalDateTime start, LocalDateTime end, boolean externalHourlyRate, WorkDay workDay) {
        setStart(start);
        setEnd(end);
        setWorkDay(workDay);
        this.externalHourlyRate = externalHourlyRate;
    }

    public boolean isExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(boolean externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

}
