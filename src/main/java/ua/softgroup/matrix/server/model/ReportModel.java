package ua.softgroup.matrix.server.model;

public class ReportModel extends TokenModel {
    private static final long serialVersionUID = 1L;

    private long id;

    private String title;

    private String discription;

    private long projectId;

    private int status;

    private boolean checked;

    public ReportModel() {
    }

    public ReportModel(long id, String token, String title, String discription) {
        super(token);
        this.id = id;
        this.title = title;
        this.discription = discription;
    }

    public ReportModel(long id, String token, String title, String discription, long projectId) {
        super(token);
        this.id = id;
        this.title = title;
        this.discription = discription;
        this.projectId = projectId;
    }

    public ReportModel(String token, String title, String discription) {
        super(token);
        this.title = title;
        this.discription = discription;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
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

    @Override
    public String toString() {
        return "ReportModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", discription='" + discription + '\'' +
                ", projectId=" + projectId +
                ", status=" + status +
                '}';
    }
}