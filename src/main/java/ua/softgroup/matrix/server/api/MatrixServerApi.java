package ua.softgroup.matrix.server.api;

import ua.softgroup.matrix.server.model.*;

import java.util.List;
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

    void saveScreenshot(ScreenshotModel file);

    Set<ReportModel> getAllReports(TokenModel tokenModel);

    Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId);

    void startWork(TokenModel tokenModel);

    void endWork(TokenModel tokenModel);

    boolean isClientSettingsUpdated(long settingsVersion);

    ClientSettingsModel getClientSettings();

}
