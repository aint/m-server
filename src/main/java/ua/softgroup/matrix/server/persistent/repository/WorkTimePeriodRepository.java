package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;

public interface WorkTimePeriodRepository extends CrudRepository<WorkTimePeriod, Long> {

    WorkTimePeriod findTopByWorkDayOrderByStartAsc(WorkDay workDay);

}
