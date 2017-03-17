package ua.softgroup.matrix.server.supervisor.producer.json;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ExecutorReportJson {

    private Long id;
    private boolean checked;
    private Long jailerId;
    private double coefficient = 1.0f;
    private String reportText;
    private int rate;
    private int currencyId;

    public ExecutorReportJson() {
    }

    public ExecutorReportJson(Long id, boolean checked, Long jailerId, double coefficient,
                              String reportText, int rate, int currencyId) {
        this.id = id;
        this.checked = checked;
        this.jailerId = jailerId;
        this.coefficient = coefficient;
        this.reportText = reportText;
        this.rate = rate;
        this.currencyId = currencyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Long getJailerId() {
        return jailerId;
    }

    public void setJailerId(Long jailerId) {
        this.jailerId = jailerId;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
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
