package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Tracking;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

public interface TrackingRepository extends CrudRepository<Tracking, Long> {

    Tracking findByWorkDay(WorkDay workDay);

}
