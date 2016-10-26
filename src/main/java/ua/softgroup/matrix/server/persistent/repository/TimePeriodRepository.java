package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.TimePeriod;

public interface TimePeriodRepository extends CrudRepository<TimePeriod, Long> {

}
