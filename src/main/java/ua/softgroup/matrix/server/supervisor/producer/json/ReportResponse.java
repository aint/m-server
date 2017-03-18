package ua.softgroup.matrix.server.supervisor.producer.json;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ReportResponse {

    private long id;
    private String entityType = "project";
    private LocalDate date;
    private LocalDateTime updatedDateTime;
    private long authorId;
    private long projectId;
    private long jailerId;
    private boolean checked;
    private double coefficient;
    private String text;
    private int workTimeSeconds;
    private int rate;
    private int currencyId;

    public ReportResponse() {
    }

    public ReportResponse(long id, LocalDate date, LocalDateTime updatedDateTime, long authorId, long projectId,
                          long jailerId, boolean checked, double coefficient, String text, int workTimeSeconds,
                          int rate, int currencyId) {
        this.id = id;
        this.date = date;
        this.updatedDateTime = updatedDateTime;
        this.authorId = authorId;
        this.projectId = projectId;
        this.jailerId = jailerId;
        this.checked = checked;
        this.coefficient = coefficient;
        this.text = text;
        this.workTimeSeconds = workTimeSeconds;
        this.rate = rate;
        this.currencyId = currencyId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getJailerId() {
        return jailerId;
    }

    public void setJailerId(long jailerId) {
        this.jailerId = jailerId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWorkTimeSeconds() {
        return workTimeSeconds;
    }

    public void setWorkTimeSeconds(int workTimeSeconds) {
        this.workTimeSeconds = workTimeSeconds;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }
}
