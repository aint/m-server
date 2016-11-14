package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.AbstractPeriod;

public interface PeriodRepository extends CrudRepository<AbstractPeriod, Long> {

}
