package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import java.time.LocalTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class Executor {

    private Long id;
    private LocalTime start;
    private LocalTime end;
    private int dayWorkTimeSeconds;
    private int idleTimeSeconds;
    private double idlePercentage;
    private Report report;

    public Executor() {
    }

    public Executor(Long id, LocalTime start, LocalTime end, int dayWorkTimeSeconds, int idleTimeSeconds,
                    double idlePercentage, Report report) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
        this.idleTimeSeconds = idleTimeSeconds;
        this.idlePercentage = idlePercentage;
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getDayWorkTimeSeconds() {
        return dayWorkTimeSeconds;
    }

    public void setDayWorkTimeSeconds(int dayWorkTimeSeconds) {
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
    }

    public int getIdleTimeSeconds() {
        return idleTimeSeconds;
    }

    public void setIdleTimeSeconds(int idleTimeSeconds) {
        this.idleTimeSeconds = idleTimeSeconds;
    }

    public double getIdlePercentage() {
        return idlePercentage;
    }

    public void setIdlePercentage(double idlePercentage) {
        this.idlePercentage = idlePercentage;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
