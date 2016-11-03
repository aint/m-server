package ua.softgroup.matrix.server.model;

import java.time.LocalDate;

public class ReportModel extends TokenModel {
    private static final long serialVersionUID = 1L;

    private long id;

    private String title;

    private String description;

    private long projectId;

    private int status;

    private boolean checked;

    private LocalDate date;

    public ReportModel() {
    }

    public ReportModel(long id, String token, String title, String description) {
        super(token);
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public ReportModel(long id, String token, String title, String description, long projectId) {
        super(token);
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
    }

    public ReportModel(String token, String title, String description) {
        super(token);
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportModel that = (ReportModel) o;

        if (id != that.id) return false;
        if (projectId != that.projectId) return false;
        if (status != that.status) return false;
        if (checked != that.checked) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (projectId ^ (projectId >>> 32));
        result = 31 * result + status;
        result = 31 * result + (checked ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", projectId=" + projectId +
                ", status=" + status +
                '}';
    }
}