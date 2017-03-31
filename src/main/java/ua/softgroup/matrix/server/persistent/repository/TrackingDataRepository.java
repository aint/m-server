package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.TrackingData;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

public interface TrackingDataRepository extends CrudRepository<TrackingData, Long> {

    TrackingData findByWorkTimePeriodWorkDay(WorkDay workDay);

}
