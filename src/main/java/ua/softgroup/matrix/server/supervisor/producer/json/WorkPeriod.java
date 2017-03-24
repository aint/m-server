package ua.softgroup.matrix.server.supervisor.producer.json;

import java.time.LocalTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class WorkPeriod {

    private LocalTime start;
    private LocalTime end;

    public WorkPeriod() {
    }

    public WorkPeriod(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

}
