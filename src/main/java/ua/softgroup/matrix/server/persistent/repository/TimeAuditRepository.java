package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.TimeAudit;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

public interface TimeAuditRepository extends CrudRepository<TimeAudit, Long> {

    Set<TimeAudit> findByAdder(User adder);

    Set<TimeAudit> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);
}
