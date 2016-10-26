package ua.softgroup.matrix.server.persistent.entity;

import com.google.gson.annotations.SerializedName;
import ua.softgroup.matrix.server.supervisor.models.RetrofitModel;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Project1")
public class Project implements RetrofitModel {

    @SerializedName("id")
    @Id
    private long id;
    @SerializedName("title")
    @Column
    private String title;
    @SerializedName("description_text")
    @Column
    private String description;
    @SerializedName("author_name")
    @Column
    private String authorName;
    @SerializedName("start_date")
    @Column
    private Date startDate;
    @SerializedName("end_date")
    @Column
    private Date endDate;
    @SerializedName("rate")
    @Column
    private int rate;
    @SerializedName("rate_currency_id")
    @Column
    private int rateCurrencyId;

    //TODO FIX THIS
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRateCurrencyId() {
        return rateCurrencyId;
    }

    public void setRateCurrencyId(int rateCurrencyId) {
        this.rateCurrencyId = rateCurrencyId;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", authorName='" + authorName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rate=" + rate +
                ", rateCurrencyId=" + rateCurrencyId +
                '}';
    }
}
