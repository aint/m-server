package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.TrackingData;

import java.time.LocalDate;
import java.util.Map;

public interface TrackingDataService extends GeneralEntityService<TrackingData> {

    TrackingData getByProjectIdAndDate(String userToken, Long projectId, LocalDate date);

    void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage,
                          Map<String, Integer> windowsTimeMap, byte[] screenshot);

}
