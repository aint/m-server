package ua.softgroup.matrix.server.persistent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Entity
public class Screenshot extends AbstractEntity<Long> {
    private static final long serialVersionUID = -5537683353413610686L;

    @Lob
    private byte[] imageBytes;

    @Column
    private LocalDateTime creationTime;

    @ManyToOne
    private TrackingData trackingData;

    public Screenshot() {
    }

    public Screenshot(byte[] imageBytes, LocalDateTime creationTime, TrackingData trackingData) {
        this.imageBytes = imageBytes;
        this.creationTime = creationTime;
        this.trackingData = trackingData;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public TrackingData getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(TrackingData trackingData) {
        this.trackingData = trackingData;
    }
}
