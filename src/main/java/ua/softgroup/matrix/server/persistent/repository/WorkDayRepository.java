package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;

import java.time.LocalDate;

public interface WorkDayRepository extends CrudRepository<WorkDay, Long> {

    WorkDay findByDateAndWorkTime(LocalDate localDate, WorkTime workTime);

}
