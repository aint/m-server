package ua.softgroup.matrix.server.supervisor.producer.json.v2;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class Executor {

    private long id;
    private String start;
    private String end;
    private int dayWorkTimeSeconds;
    private int idleTimeSeconds;
    private double idlePercentage;
    private Report reports;

    public Executor() {
    }

    public Executor(Long id, String start, String end, int dayWorkTimeSeconds, int idleTimeSeconds,
                    double idlePercentage, Report reports) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
        this.idleTimeSeconds = idleTimeSeconds;
        this.idlePercentage = idlePercentage;
        this.reports = reports;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
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

    public Report getReports() {
        return reports;
    }

    public void setReports(Report reports) {
        this.reports = reports;
    }
}
