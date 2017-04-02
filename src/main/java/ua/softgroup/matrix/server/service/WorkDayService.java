package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface WorkDayService extends GeneralEntityService<WorkDay> {

    int getTotalWorkSeconds(User author, Project project);

    int getTotalWorkSeconds(User author, LocalDate date);

    int getCurrentMonthIdleSeconds(User author, Project project);

    int getTotalIdleSeconds(User author, Project project);

    int getTotalIdleSeconds(User author, LocalDate date);

    Optional<WorkDay> getByAuthorAndProjectAndDate(User author, Project project, LocalDate localDate);

    Set<WorkDay> getAllWorkDaysOf(Long userId, Long projectSupervisorId, LocalDate from, LocalDate to);

    Set<WorkDay> getAllWorkDaysOf(User user, LocalDate localDate);

    Set<WorkDay> getAllWorkDaysOf(Long projectSupervisorId, LocalDate date);

    Set<WorkDay> getProjectWorkDaysBetween(Long projectSupervisorId, LocalDate from, LocalDate to);

    Set<WorkDay> getUserWorkDaysBetween(Long userId, LocalDate from, LocalDate to);

    Set<WorkDay> getUserNotCheckedWorkDays(Long userId);

    Set<WorkDay> getProjectNotCheckedWorkDays(Long projectSupervisorId);

    Set<WorkDay> getAllNotCheckedWorkDays();

    Set<WorkDay> getWorkDaysBetween(LocalDate from, LocalDate to);

    Set<ReportModel> getWorkDaysOf(String userToken, Long projectId);

    ResponseStatus saveReportOrUpdate(String userToken, Long projectId, ReportModel reportModel);

    LocalTime getStartWorkOf(WorkDay workDay);

    LocalTime getEndWorkOf(WorkDay workDay);

}
