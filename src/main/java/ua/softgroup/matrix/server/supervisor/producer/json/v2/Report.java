package ua.softgroup.matrix.server.supervisor.producer.json.v2;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class Report {

    private Long id;
    private Long entityId;
    private String entityType = "project";
    private boolean checked;
    private Long checkedById;
    private double coefficient = 1.0f;
    private String text;
    private int dayWorkTimeSeconds;
    private int rate;
    private int currencyId;

    public Report() {
    }

    public Report(Long id, Long entityId, boolean checked, Long checkedById, double coefficient,
                  String text, int dayWorkTimeSeconds, int rate, int currencyId) {
        this.id = id;
        this.entityId = entityId;
        this.checked = checked;
        this.checkedById = checkedById;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Long getCheckedById() {
        return checkedById;
    }

    public void setCheckedById(Long checkedById) {
        this.checkedById = checkedById;
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
