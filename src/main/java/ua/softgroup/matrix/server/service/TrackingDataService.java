package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.api.model.datamodels.ActiveWindowModel;
import ua.softgroup.matrix.server.persistent.entity.TrackingData;

import java.util.List;

public interface TrackingDataService extends GeneralEntityService<TrackingData> {

    void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage,
                          List<ActiveWindowModel> activeWindowList, byte[] screenshot, String screenshotTitle);

}
