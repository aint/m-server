package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;

public interface ProjectRepository extends CrudRepository<Project, Long> {
}
