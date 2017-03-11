package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class TimeAudit extends AbstractEntity<Long> {
    private static final long serialVersionUID = 7093407748878141348L;

    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column
    private Integer addedMinutes;

    @Column
    private String reason;

    @OneToOne
    private User adder;

    @OneToOne
    private WorkDay workDay;

    public TimeAudit() {
    }

    public TimeAudit(Integer addedMinutes, String reason, User adder, WorkDay workDay) {
        this.addedMinutes = addedMinutes;
        this.reason = reason;
        this.adder = adder;
        this.workDay = workDay;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getAddedMinutes() {
        return addedMinutes;
    }

    public void setAddedMinutes(Integer addedMinutes) {
        this.addedMinutes = addedMinutes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getAdder() {
        return adder;
    }

    public void setAdder(User adder) {
        this.adder = adder;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDays) {
        this.workDay = workDays;
    }
}
