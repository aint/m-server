package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class WorktimePeriod extends AbstractPeriod {
    private static final long serialVersionUID = 2140610419978157701L;

    public WorktimePeriod() {
    }

    public WorktimePeriod(LocalDateTime start, LocalDateTime end, WorkDay workDay) {
        setStart(start);
        setEnd(end);
        setWorkDay(workDay);
    }

}
