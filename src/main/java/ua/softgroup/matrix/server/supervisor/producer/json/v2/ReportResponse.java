package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import java.time.LocalDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ReportResponse {

    private long id;
    private String entityType = "project";
    private LocalDate date;
    private long authorId;
    private long entityId;
    private long checkedById;
    private boolean checked;
    private double coefficient;
    private String text;
    private int dayWorkTimeSeconds;
    private int rate;
    private int currencyId;

    public ReportResponse() {
    }

    public ReportResponse(long id, LocalDate date, long authorId, long entityId, long checkedById,
                          boolean checked, double coefficient, String text, int dayWorkTimeSeconds,
                          int rate, int currencyId) {
        this.id = id;
        this.date = date;
        this.authorId = authorId;
        this.entityId = entityId;
        this.checkedById = checkedById;
        this.checked = checked;
        this.coefficient = coefficient;
        this.text = text;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
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

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getCheckedById() {
        return checkedById;
    }

    public void setCheckedById(long checkedById) {
        this.checkedById = checkedById;
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

    public int getDayWorkTimeSeconds() {
        return dayWorkTimeSeconds;
    }

    public void setDayWorkTimeSeconds(int dayWorkTimeSeconds) {
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
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
