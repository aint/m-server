package ua.softgroup.matrix.server.supervisor.jersey.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TimeJson {

    private Long todayMinutes;
    private Long totalMinutes;

    public TimeJson() {
    }

    public TimeJson(Long todayMinutes, Long totalMinutes) {
        this.todayMinutes = todayMinutes;
        this.totalMinutes = totalMinutes;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Long getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(Long todayMinutes) {
        this.todayMinutes = todayMinutes;
    }

    @Override
    public String toString() {
        return "TimeJson{" +
                "todayMinutes=" + todayMinutes +
                ", totalMinutes=" + totalMinutes +
                '}';
    }
}
