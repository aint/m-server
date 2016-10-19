package ua.softgroup.matrix.server.api;

import java.time.LocalDate;
import java.util.List;

public class MatrixServerApiImpl implements MatrixServerApi {

    @Override
    public String authenticate(String credentials) {
        System.out.println("- service layer: authenticate: " + credentials + "\n");
        return null;
    }

    @Override
    public List<Object> getAllProjects() {
        return null;
    }

    @Override
    public void setCurrentProject(Long projectId) {

    }

    @Override
    public String getReport(Long reportId) {
        return null;
    }

    @Override
    public Long saveReport(String report, LocalDate date) {
        System.out.println("- service layer: saveReport: " + report + date + "\n");
        return null;
    }
}
