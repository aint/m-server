package ua.softgroup.matrix.server.service;

import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public interface WorkDayService extends GeneralEntityService<WorkDay> {

    Optional<WorkDay> getByDateAndProject(LocalDate localDate, Project project);

}
