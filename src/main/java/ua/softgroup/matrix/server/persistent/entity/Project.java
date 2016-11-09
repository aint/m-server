package ua.softgroup.matrix.server.persistent.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.softgroup.matrix.server.supervisor.models.RetrofitModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project implements RetrofitModel, Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @Id
    private long id;
    @JsonProperty("title")
    @Column(columnDefinition = "TEXT")
    private String title;
    @JsonProperty("description_text")
    @Column
    private String description;
    @JsonProperty("author_name")
    @Column
    private String authorName;
    @JsonProperty("start_date")
    @Column
    private LocalDate startDate;
    @JsonProperty("end_date")
    @Column
    private LocalDate endDate;
    @JsonProperty("rate")
    @Column
    private int rate;
    @JsonProperty("rate_currency_id")
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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
