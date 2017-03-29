package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public abstract class TimeResponse {

    protected int totalWorkTimeSeconds;
    protected int totalIdleTimeSeconds;
    protected double totalIdlePercentage;

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
}
