package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface WorkDayService extends GeneralEntityService<WorkDay> {

    Optional<WorkDay> getByDateAndProject(LocalDate localDate, Project project);

    Set<WorkDay> getAllWorkDaysOf(User user, Project project);

    Set<ReportModel> getWorkDaysOf(String userToken, Long projectId);

    ResponseStatus saveReportOrUpdate(String userToken, ReportModel reportModel);

    ReportJson convertEntityToJson(WorkDay workDay);

}
