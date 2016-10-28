package ua.softgroup.matrix.server.model;

import java.io.Serializable;

public class SynchronizedModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private ReportModel reportModel;
    private TimeModel timeModel;
    private TimeModel downtimeModel;

    public ReportModel getReportModel() {
        return reportModel;
    }

    public void setReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;
    }

    public TimeModel getTimeModel() {
        return timeModel;
    }

    public void setTimeModel(TimeModel timeModel) {
        this.timeModel = timeModel;
    }

    public TimeModel getDowntimeModel() {
        return downtimeModel;
    }

    public void setDowntimeModel(TimeModel downtimeModel) {
        this.downtimeModel = downtimeModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynchronizedModel that = (SynchronizedModel) o;

        if (reportModel != null ? !reportModel.equals(that.reportModel) : that.reportModel != null) return false;
        if (timeModel != null ? !timeModel.equals(that.timeModel) : that.timeModel != null) return false;
        return downtimeModel != null ? downtimeModel.equals(that.downtimeModel) : that.downtimeModel == null;

    }

    @Override
    public int hashCode() {
        int result = reportModel != null ? reportModel.hashCode() : 0;
        result = 31 * result + (timeModel != null ? timeModel.hashCode() : 0);
        result = 31 * result + (downtimeModel != null ? downtimeModel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SynchronizedModel{" +
                "reportModel=" + reportModel +
                ", timeModel=" + timeModel +
                ", downtimeModel=" + downtimeModel +
                '}';
    }
}
