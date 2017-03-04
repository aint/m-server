package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import java.util.Set;

public interface ReportService extends GeneralEntityService<Report> {

    Set<Report> getAllReportsOf(User user, Project project);

    Set<ReportModel> getReportsOf(String userToken, Long projectId);

    boolean ifReportExistForToday(String userToken, Long projectId);

    ResponseStatus saveOrUpdate(String userToken, ReportModel reportModel);

    ReportJson convertEntityToJson(Report report);

}
