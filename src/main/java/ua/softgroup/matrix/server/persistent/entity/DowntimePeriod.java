package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class DowntimePeriod extends AbstractPeriod {
    private static final long serialVersionUID = -6566643418328434191L;

    public DowntimePeriod() {
    }

    public DowntimePeriod(LocalDateTime start, LocalDateTime end, WorkTime workTime) {
        setStart(start);
        setEnd(end);
        setWorkTime(workTime);
    }

}
