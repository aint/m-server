package ua.softgroup.matrix.server.persistent.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;

import java.time.LocalDate;
import java.util.Set;

public interface WorkDayRepository extends CrudRepository<WorkDay, Long> {

    Set<WorkDay> findByAuthorAndProject(User author, Project project);

    WorkDay findByAuthorAndProjectAndDate(User author, Project project, LocalDate localDate);

    @Query(value = "SELECT sum(work_seconds) FROM work_day WHERE project_id = :projectId and author_id = :userId", nativeQuery = true)
    int getTotalWorkSeconds(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query(value = "SELECT sum(idle_seconds) FROM work_day WHERE project_id = :projectId and author_id = :userId", nativeQuery = true)
    int getCurrentMonthIdleSeconds(@Param("userId") Long userId, @Param("projectId") Long projectId);

}
