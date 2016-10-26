package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;

import java.util.Set;

public interface WorkTimeRepository extends CrudRepository<WorkTime, Long> {

    WorkTime findByUserAndProject(User user, Project project);

}
