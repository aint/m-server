package ua.softgroup.matrix.server.supervisor.producer.json.tracking;

import com.fasterxml.jackson.annotation.JsonView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class GeneralWorkDataJson {

    @JsonView(TrackingDataViewType.USER.class)
    private Long userId;

    @JsonView(TrackingDataViewType.PROJECT.class)
    private Long entityId;
    @JsonView(TrackingDataViewType.PROJECT.class)
    private String entityType = "project";

    @JsonView(TrackingDataViewType.DATE.class)
    private LocalDate date;

    private LocalTime start;
    private LocalTime end;
    private int dayWorkTimeSeconds;
    private List<TrackingPeriodJson> periods;

    public GeneralWorkDataJson() {
    }

    public GeneralWorkDataJson(LocalDate date, LocalTime start, LocalTime end, int dayWorkTimeSeconds,
                               List<TrackingPeriodJson> periods) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
        this.periods = periods;
    }

    public GeneralWorkDataJson(Long userId, LocalTime start, LocalTime end, int dayWorkTimeSeconds,
                               List<TrackingPeriodJson> periods) {
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
        this.periods = periods;
    }

    public GeneralWorkDataJson(Long entityId, String entityType, LocalTime start, LocalTime end,
                               int dayWorkTimeSeconds, List<TrackingPeriodJson> periods) {
        this.entityId = entityId;
//        this.entityType = entityType;
        this.start = start;
        this.end = end;
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
        this.periods = periods;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public int getDayWorkTimeSeconds() {
        return dayWorkTimeSeconds;
    }

    public void setDayWorkTimeSeconds(int dayWorkTimeSeconds) {
        this.dayWorkTimeSeconds = dayWorkTimeSeconds;
    }

    public List<TrackingPeriodJson> getPeriods() {
        return periods;
    }

    public void setPeriods(List<TrackingPeriodJson> periods) {
        this.periods = periods;
    }
}
