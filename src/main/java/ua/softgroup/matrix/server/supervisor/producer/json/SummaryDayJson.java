package ua.softgroup.matrix.server.supervisor.producer.json;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class SummaryDayJson {

    private LocalDate date;
    private int totalWorkSeconds;
    private int totalIdleSeconds;
    private double totalIdlePercentage;
    private Set<DayJson> workDays;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalWorkSeconds() {
        return totalWorkSeconds;
    }

    public void setTotalWorkSeconds(int totalWorkSeconds) {
        this.totalWorkSeconds = totalWorkSeconds;
    }

    public int getTotalIdleSeconds() {
        return totalIdleSeconds;
    }

    public void setTotalIdleSeconds(int totalIdleSeconds) {
        this.totalIdleSeconds = totalIdleSeconds;
    }

    public double getTotalIdlePercentage() {
        return totalIdlePercentage;
    }

    public void setTotalIdlePercentage(double totalIdlePercentage) {
        this.totalIdlePercentage = totalIdlePercentage;
    }

    public Set<DayJson> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<DayJson> workDays) {
        this.workDays = workDays;
    }
}
