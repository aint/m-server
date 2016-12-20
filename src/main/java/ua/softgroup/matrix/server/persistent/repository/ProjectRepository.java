package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Project findBySupervisorIdAndUser(Long supervisorId, User user);
    
    List<Project> findByUser(User user);
}
