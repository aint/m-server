package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;

import java.util.Set;

public interface ReportRepository extends CrudRepository<Report, Long> {

    Set<Report> findByAuthor(User author);

    Set<Report> findByAuthorAndProject(User author, Project project);
}
