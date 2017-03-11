package ua.softgroup.matrix.server.supervisor.producer.json;

import com.fasterxml.jackson.annotation.JsonView;

import java.time.LocalDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TimeJson {

    @JsonView(JsonViewType.OUT.class)
    private Integer todayMinutes;

    @JsonView({ JsonViewType.OUT.class, JsonViewType.IN.class })
    private Integer totalMinutes;

    @JsonView(JsonViewType.IN.class)
    private String reason;

    @JsonView(JsonViewType.IN.class)
    private LocalDate date;

    public TimeJson(Integer todayMinutes, Integer totalMinutes) {
        this.todayMinutes = todayMinutes;
        this.totalMinutes = totalMinutes;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Integer getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(Integer todayMinutes) {
        this.todayMinutes = todayMinutes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TimeJson{" +
                "todayMinutes=" + todayMinutes +
                ", totalMinutes=" + totalMinutes +
                ", reason='" + reason + '\'' +
                ", date=" + date +
                '}';
    }
}
