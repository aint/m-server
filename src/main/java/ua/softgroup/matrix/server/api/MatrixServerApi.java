package ua.softgroup.matrix.server.api;

import ua.softgroup.matrix.server.model.ClientSettingsModel;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.SynchronizedModel;
import ua.softgroup.matrix.server.model.TimeModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.model.WriteKeyboard;
import ua.softgroup.matrix.server.persistent.entity.Project;

import java.util.Set;

public interface MatrixServerApi {

    /**
     * Tries to authenticate a user using the given credentials
     *
     * @param login encrypted login
     * @param password encrypted password
     * @return a token in the case of successful authentication, 'invalid credentials' otherwise
     */
    String authenticate(String login, String password);

    /**
     * Returns all projects of the authenticated user
     *
     * @return a list of projects
     */
    Set<ProjectModel> getAllProjects(TokenModel tokenModel);

    Set<Project> getUserActiveProjects(TokenModel tokenModel);

    /**
     * Sets project as current for the authenticated user
     *
     * @param projectId a project's id
     */
    void setCurrentProject(Long projectId);

    /**
     * Returns a report for the given id
     *
     * @param reportRequest the report' id and token
     * @return the report's text
     */
    @Deprecated
    ReportModel getReport(ReportModel reportRequest);

    /**
     * Saves a report for the given date
     *
     * @param reportModel report's model
     * @return an id of the saved report
     */
    Constants saveReport(ReportModel reportModel);

    Set<ReportModel> getAllReports(TokenModel tokenModel);

    Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId);

    void startWork(TimeModel timeModel);

    void endWork(TimeModel timeModel);

    void startDowntime(TimeModel downTimeModel);

    void endDowntime(TimeModel downTimeModel);

    TimeModel getTodayWorkTime(TimeModel timeModel);

    TimeModel getTotalWorkTime(TimeModel timeModel);

    boolean sync(SynchronizedModel synchronizedModel);

    boolean isClientSettingsUpdated(long settingsVersion);

    ClientSettingsModel getClientSettings();

    void saveKeyboardLog(WriteKeyboard writeKeyboard);

    void saveScreenshot(ScreenshotModel file);

}
