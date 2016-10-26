package ua.softgroup.matrix.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class TimeModel extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private long projectId;
    private long minute, hours;
    private boolean foreignRate = false;

    public TimeModel(TokenModel tokenModel) {
        super(tokenModel.getToken());
    }

    public TimeModel(TokenModel token, long minute, long hours, boolean foreignRate) {
        super(token.getToken());
        this.minute = minute;
        this.hours = hours;
        this.foreignRate = foreignRate;
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

    @Override
    public String toString() {
        return "TimeModel{" +
                "projectId=" + projectId +
                ", minute=" + minute +
                ", hours=" + hours +
                ", foreignRate=" + foreignRate +
                '}';
    }
}
