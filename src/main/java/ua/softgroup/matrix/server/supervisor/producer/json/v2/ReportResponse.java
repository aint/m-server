package ua.softgroup.matrix.server.supervisor.producer.json.v2;

import java.time.LocalDate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ReportResponse {

    private Long id;
    private String entityType = "project";
    private LocalDate date;
    private Long authorId;
    private Long entityId;
    private Long checkedById;
    private boolean checked;
    private double coefficient;
    private String text;
    private int dayWorkTimeSeconds;
    private int rate;
    private int currencyId;

    public ReportResponse() {
    }

    public ReportResponse(Long id, LocalDate date, Long authorId, Long entityId, Long checkedById,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getCheckedById() {
        return checkedById;
    }

    public void setCheckedById(Long checkedById) {
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
