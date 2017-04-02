package ua.softgroup.matrix.server.supervisor.producer.json.tracking;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TrackingDataJson {

    private LocalDate date;
    private List<GeneralWorkDataJson> workData;

    public TrackingDataJson() {
    }

    public TrackingDataJson(LocalDate date, List<GeneralWorkDataJson> workData) {
        this.date = date;
        this.workData = workData;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<GeneralWorkDataJson> getWorkData() {
        return workData;
    }

    public void setWorkData(List<GeneralWorkDataJson> workData) {
        this.workData = workData;
    }
}
