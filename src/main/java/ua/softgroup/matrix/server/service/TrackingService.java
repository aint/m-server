package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Tracking;

import java.time.LocalDate;

public interface TrackingService extends GeneralEntityService<Tracking> {

    Tracking getByProjectIdAndDate(Long projectId, LocalDate date);

}
