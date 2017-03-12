package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Tracking;

import java.time.LocalDate;
import java.util.Map;

public interface TrackingService extends GeneralEntityService<Tracking> {

    Tracking getByProjectIdAndDate(String userToken, Long projectId, LocalDate date);

    void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage, Map<String, Integer> windowsTimeMap, byte[] screenshot);

}
