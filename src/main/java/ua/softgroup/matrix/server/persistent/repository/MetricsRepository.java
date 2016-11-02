package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Metrics;

public interface MetricsRepository extends CrudRepository<Metrics, Long> {

}
