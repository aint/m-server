package ua.softgroup.matrix.server.persistent.databasemodels;

import javax.persistence.*;

@Entity
@Table(name = "Reports")
public class ReportModel extends TokenModel {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "Report_Title")
    private String title;

    @Column(name = "Report_Description")
    private String discription;

    @Column(name = "Report_Status_Code")
    private int status;

    public ReportModel() {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "title='" + title + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
