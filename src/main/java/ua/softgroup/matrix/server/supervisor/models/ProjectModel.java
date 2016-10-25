package ua.softgroup.matrix.server.supervisor.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Vadim on 24.10.2016.
 */
public class ProjectModel implements RetrofitModel {

    @SerializedName("id") private int id;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("author_name") private String authorName;
    @SerializedName("start_date") private Date startDate;
    @SerializedName("end_date") private Date endDate;
    @SerializedName("rate") private int rate;
    @SerializedName("rate_currency_id") private int rateCurrencyId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
