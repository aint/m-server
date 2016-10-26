package ua.softgroup.matrix.server.model;

import java.io.Serializable;

public class SynchronizedModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private ReportModel reportModel;
    private TimeModel timeModel;

    public SynchronizedModel(ReportModel reportModel, TimeModel timeModel) {
        this.timeModel = timeModel;
        this.reportModel = reportModel;
    }

    public TimeModel getTimeModel() {
        return timeModel;
    }

    public void setTimeModel(TimeModel timeModel) {
        this.timeModel = timeModel;
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public void setReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynchronizedModel that = (SynchronizedModel) o;

        if (reportModel != null ? !reportModel.equals(that.reportModel) : that.reportModel != null) return false;
        return timeModel != null ? timeModel.equals(that.timeModel) : that.timeModel == null;
    }

    @Override
    public int hashCode() {
        int result = reportModel != null ? reportModel.hashCode() : 0;
        result = 31 * result + (timeModel != null ? timeModel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SynchronizedModel{" +
                "reportModel=" + reportModel +
                ", timeModel=" + timeModel +
                '}';
    }
}
