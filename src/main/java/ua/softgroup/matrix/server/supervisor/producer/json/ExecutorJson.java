package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ExecutorJson {

    private Long id;
    private String start;
    private String end;
    private int workSeconds;
    private int idleSeconds;
    private double idlePercentage;
    private ExecutorReportJson executorReport;

    public ExecutorJson() {
    }

    public ExecutorJson(Long id, String start, String end, int workSeconds, int idleSeconds,
                        double idlePercentage, ExecutorReportJson executorReport) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.workSeconds = workSeconds;
        this.idleSeconds = idleSeconds;
        this.idlePercentage = idlePercentage;
        this.executorReport = executorReport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getWorkSeconds() {
        return workSeconds;
    }

    public void setWorkSeconds(int workSeconds) {
        this.workSeconds = workSeconds;
    }

    public int getIdleSeconds() {
        return idleSeconds;
    }

    public void setIdleSeconds(int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    public double getIdlePercentage() {
        return idlePercentage;
    }

    public void setIdlePercentage(double idlePercentage) {
        this.idlePercentage = idlePercentage;
    }

    public ExecutorReportJson getExecutorReport() {
        return executorReport;
    }

    public void setExecutorReport(ExecutorReportJson executorReport) {
        this.executorReport = executorReport;
    }
}
