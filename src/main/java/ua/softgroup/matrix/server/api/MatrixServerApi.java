package ua.softgroup.matrix.server.api;

import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;

import java.util.List;

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
    List<Object> getAllProjects();

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
    ReportModel getReport(ReportModel reportRequest);

    /**
     * Saves a report for the given date
     *
     * @param reportModel report's model
     * @return an id of the saved report
     */
    Constants saveReport(ReportModel reportModel);

    void saveScreenshot(ScreenshotModel file);

}
