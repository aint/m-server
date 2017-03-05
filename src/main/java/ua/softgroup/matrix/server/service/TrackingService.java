package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Tracking;

import java.time.LocalDate;
import java.util.Map;

public interface TrackingService extends GeneralEntityService<Tracking> {

    Tracking getByProjectIdAndDate(Long projectId, LocalDate date);

    void saveTrackingData(Long projectId, String keyboardText, Integer mouseFootage, Map<String, Integer> windowsTimeMap, byte[] screenshot);

}
