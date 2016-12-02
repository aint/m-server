package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Deprecated
@Entity
public class DowntimePeriod extends AbstractPeriod {
    private static final long serialVersionUID = -6566643418328434191L;

    public DowntimePeriod() {
    }

    public DowntimePeriod(LocalDateTime start, LocalDateTime end, WorkDay workDay) {
        setStart(start);
        setEnd(end);
        setWorkDay(workDay);
    }

}
