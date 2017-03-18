package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.supervisor.producer.json.ReportJson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface WorkDayService extends GeneralEntityService<WorkDay> {

    int getTotalWorkSeconds(User author, Project project);

    int getCurrentMonthIdleSeconds(User author, Project project);

    Optional<WorkDay> getByAuthorAndProjectAndDate(User author, Project project, LocalDate localDate);

    Set<WorkDay> getAllWorkDaysOf(User user, Project project);

    Set<WorkDay> getAllWorkDaysOf(User user, LocalDate localDate);

    Set<WorkDay> getAllWorkDaysOf(Long projectId, LocalDate date);

    Set<WorkDay> getProjectWorkDaysBetween(Long projectSupervisorId, LocalDate from, LocalDate to);

    Set<WorkDay> getUserWorkDaysBetween(Long userId, LocalDate from, LocalDate to);

    Set<WorkDay> getWorkDaysBetween(LocalDate from, LocalDate to);

    Set<ReportModel> getWorkDaysOf(String userToken, Long projectId);

    ResponseStatus saveReportOrUpdate(String userToken, Long projectId, ReportModel reportModel);

    LocalDateTime getStartWorkOf(WorkDay workDay);

    LocalDateTime getEndWorkOf(WorkDay workDay);

    ReportJson convertEntityToJson(WorkDay workDay);

}
