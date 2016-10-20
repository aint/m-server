package ua.softgroup.matrix.server.model;

public class ReportModel extends TokenModel {
    private static final long serialVersionUID = 1L;
    private long id;
    private String title, discription;
    private int status;

    public ReportModel(String token, String title, String discription) {
        super(token);
        this.title = title;
        this.discription = discription;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

}