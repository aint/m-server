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

    Set<WorkDay> findByAuthorAndDate(User author, LocalDate date);

    Set<WorkDay> findByProjectIdAndDate(Long projectId, LocalDate date);

    Set<WorkDay> findByProjectSupervisorIdAndDateBetween(Long supervisorId, LocalDate from, LocalDate to);

    Set<WorkDay> findByAuthorIdAndDateBetween(Long authorId, LocalDate from, LocalDate to);

    Set<WorkDay> findByAuthorIdAndCheckedFalse(Long authorId);

    Set<WorkDay> findByProjectSupervisorIdAndCheckedFalse(Long supervisorId);

    Set<WorkDay> findByCheckedFalse();

    Set<WorkDay> findByDateBetween(LocalDate from, LocalDate to);

    WorkDay findByAuthorAndProjectAndDate(User author, Project project, LocalDate localDate);

    @Query(value = "SELECT sum(work_seconds) FROM work_day WHERE project_id = :projectId and author_id = :userId", nativeQuery = true)
    Integer getTotalWorkSeconds(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query(value = "SELECT sum(idle_seconds) FROM work_day "
                                          + "WHERE project_id = :projectId "
                                                 + "AND author_id = :userId "
                                                 + "AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer getCurrentMonthIdleSeconds(@Param("userId") Long userId, @Param("projectId") Long projectId,
                                       @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
