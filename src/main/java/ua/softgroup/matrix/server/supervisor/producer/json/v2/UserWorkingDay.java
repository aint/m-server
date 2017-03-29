package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class UserWorkingDay {

    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private int totalWorkTimeSeconds;
    private int totalIdleTimeSeconds;
    private double totalIdlePercentage;
    private Set<Period> periods;
    private Set<Report> reports;

    public UserWorkingDay() {
    }

    public UserWorkingDay(LocalDate date, LocalTime start, LocalTime end, int totalWorkTimeSeconds, int totalIdleTimeSeconds,
                          double totalIdlePercentage, Set<Period> periods, Set<Report> reports) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.totalWorkTimeSeconds = totalWorkTimeSeconds;
        this.totalIdleTimeSeconds = totalIdleTimeSeconds;
        this.totalIdlePercentage = totalIdlePercentage;
        this.periods = periods;
        this.reports = reports;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getTotalWorkTimeSeconds() {
        return totalWorkTimeSeconds;
    }

    public void setTotalWorkTimeSeconds(int totalWorkTimeSeconds) {
        this.totalWorkTimeSeconds = totalWorkTimeSeconds;
    }

    public int getTotalIdleTimeSeconds() {
        return totalIdleTimeSeconds;
    }

    public void setTotalIdleTimeSeconds(int totalIdleTimeSeconds) {
        this.totalIdleTimeSeconds = totalIdleTimeSeconds;
    }

    public double getTotalIdlePercentage() {
        return totalIdlePercentage;
    }

    public void setTotalIdlePercentage(double totalIdlePercentage) {
        this.totalIdlePercentage = totalIdlePercentage;
    }

    public Set<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<Period> periods) {
        this.periods = periods;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
}
