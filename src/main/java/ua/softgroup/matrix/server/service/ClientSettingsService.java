package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.service.impl.TrackerSettings;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface ClientSettingsService {

    TrackerSettings getTrackerSettings(String token);

    int getReportEditableDays();

}
