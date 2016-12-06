package ua.softgroup.matrix.server.persistent.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class TimeAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column
    private Long addedMinutes;

    @Column
    private String reason;

    @OneToOne
    private User adder;

    @OneToOne
    private WorkDay workDay;

    public TimeAudit() {
    }

    public TimeAudit(Long addedMinutes, String reason, User adder, WorkDay workDay) {
        this.addedMinutes = addedMinutes;
        this.reason = reason;
        this.adder = adder;
        this.workDay = workDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getAddedMinutes() {
        return addedMinutes;
    }

    public void setAddedMinutes(Long addedMinutes) {
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
