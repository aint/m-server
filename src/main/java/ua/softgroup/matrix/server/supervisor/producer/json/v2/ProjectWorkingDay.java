package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ProjectWorkingDay {

    private LocalDate date;
    private int totalDayWorkTimeSeconds;
    private int totalIdleTimeSeconds;
    private double totalIdlePercentage;
    private Set<Executor> executors;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalDayWorkTimeSeconds() {
        return totalDayWorkTimeSeconds;
    }

    public void setTotalDayWorkTimeSeconds(int totalDayWorkTimeSeconds) {
        this.totalDayWorkTimeSeconds = totalDayWorkTimeSeconds;
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

    public Set<Executor> getExecutors() {
        return executors;
    }

    public void setExecutors(Set<Executor> executors) {
        this.executors = executors;
    }
}
