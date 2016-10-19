package ua.softgroup.matrix.server.api;

import java.time.LocalDate;
import java.util.List;

public interface MatrixServerApi {

    /**
     * Tries to authenticate a user using the given credentials
     *
     * @param credentials encrypted login and password of the user
     * @return a token in the case of successful authentication, 'invalid credentials' otherwise
     */
    String authenticate(String credentials);

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
     * @param reportId the report' id
     * @return the report's text
     */
    String getReport(Long reportId);

    /**
     * Saves a report for the given date
     *
     * @param report the report's text
     * @param date the report's date
     * @return an id of the saved report
     */
    Long saveReport(String report, LocalDate date);

}
