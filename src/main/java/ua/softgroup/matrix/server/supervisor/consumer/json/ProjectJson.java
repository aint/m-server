package ua.softgroup.matrix.server.supervisor.consumer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ProjectJson {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String title;

    @JsonProperty("description_text")
    private String description;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty
    private Integer rate;

    @JsonProperty("rate_currency_id")
    private Integer rateCurrencyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRateCurrencyId() {
        return rateCurrencyId;
    }

    public void setRateCurrencyId(Integer rateCurrencyId) {
        this.rateCurrencyId = rateCurrencyId;
    }

    @Override
    public String toString() {
        return "ProjectJson{" +
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
