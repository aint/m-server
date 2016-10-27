package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Downtime;

public interface DowntimeRepository extends CrudRepository<Downtime, Long> {

}
