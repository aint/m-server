package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;

public class TimeModel extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private long projectId;
    private long minute, hours;
    private boolean foreignRate = false;
    private boolean isDownTime = false;
    private double percentDownTime;

    public TimeModel(long hours, long minute) {
        this.hours = hours;
        this.minute = minute;
    }

    public TimeModel(long hours, long minute, double percentDownTime) {
        this.hours = hours;
        this.minute = minute;
        this.percentDownTime = percentDownTime;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public boolean isForeignRate() {
        return foreignRate;
    }

    public void setForeignRate(boolean foreignRate) {
        this.foreignRate = foreignRate;
    }

    public boolean isDownTime() {
        return isDownTime;
    }

    public void setDownTime(boolean downTime) {
        isDownTime = downTime;
    }

    public double getPercentDownTime() {
        return percentDownTime;
    }

    public void setPercentDownTime(double percentDownTime) {
        this.percentDownTime = percentDownTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeModel timeModel = (TimeModel) o;

        if (projectId != timeModel.projectId) return false;
        if (minute != timeModel.minute) return false;
        if (hours != timeModel.hours) return false;
        return foreignRate == timeModel.foreignRate;

    }

    @Override
    public int hashCode() {
        int result = (int) (projectId ^ (projectId >>> 32));
        result = 31 * result + (int) (minute ^ (minute >>> 32));
        result = 31 * result + (int) (hours ^ (hours >>> 32));
        result = 31 * result + (foreignRate ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TimeModel{" +
                "projectId=" + projectId +
                ", minute=" + minute +
                ", hours=" + hours +
                ", foreignRate=" + foreignRate +
                ", isDownTime=" + isDownTime +
                ", percentDownTime=" + percentDownTime +
                '}';
    }
}
