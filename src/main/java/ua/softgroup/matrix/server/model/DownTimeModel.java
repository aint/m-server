package ua.softgroup.matrix.server.model;

public class DownTimeModel extends TimeModel {
    private static final long serialVersionUID = 1L;
    private boolean isDownTime;

    public DownTimeModel(TokenModel token) {
        super(token);
    }

    public boolean isDownTime() {
        return isDownTime;
    }

    public void setDownTime(boolean downTime) {
        isDownTime = downTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DownTimeModel that = (DownTimeModel) o;

        return isDownTime == that.isDownTime;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isDownTime ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DownTimeModel{" +
                "projectId=" + projectId +
                ", minute=" + minute +
                ", hours=" + hours +
                ", foreignRate=" + foreignRate +
                ", isDownTime=" + isDownTime +
                '}';
    }
}
