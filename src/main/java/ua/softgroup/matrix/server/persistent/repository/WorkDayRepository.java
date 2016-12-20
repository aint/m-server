package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import java.time.LocalDate;

public interface WorkDayRepository extends CrudRepository<WorkDay, Long> {

    WorkDay findByDateAndProject(LocalDate localDate, Project project);

}
