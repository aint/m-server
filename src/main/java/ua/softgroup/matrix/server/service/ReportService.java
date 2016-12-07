package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import java.util.Set;

public interface ReportService extends GeneralEntityService<Report> {

    Set<Report> getAllReportsOf(User user);

    Set<Report> getAllReportsOf(User user, Project project);

    Set<Report> getTodayReportsOf(User author, Project project);

    Report save(ReportModel reportModel);

    ReportModel convertEntityToDto(Report report, String token);

    ReportJson convertEntityToJson(Report report);

}
