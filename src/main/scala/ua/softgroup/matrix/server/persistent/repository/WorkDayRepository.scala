package ua.softgroup.matrix.server.persistent.repository

import java.time.LocalDate
import java.util
import java.util.Set

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ua.softgroup.matrix.server.persistent.entity.{Project, User, WorkDay}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait WorkDayRepository extends CrudRepository[WorkDay, java.lang.Long] {

  def findByAuthorAndProject(author: User, project: Project): util.Set[WorkDay]

  def findByDate(date: LocalDate): util.Set[WorkDay]

  @Query(value = "SELECT sum(work_seconds) FROM work_day " +
                 "WHERE author_id = :userId and date = :date", nativeQuery = true)
  def getTotalWorkSeconds(@Param("userId") userId: Long, @Param("date") date: LocalDate): Integer

  @Query(value = "SELECT sum(idle_seconds) FROM work_day " +
                 "WHERE author_id = :userId and date = :date", nativeQuery = true)
  def getTotalIdleSeconds(@Param("userId") userId: Long, @Param("date") date: LocalDate): Integer

  def findByAuthorAndDate(author: User, date: LocalDate): util.Set[WorkDay]

  def findByProjectSupervisorIdAndDate(projectSupervisorId: Long, date: LocalDate): util.Set[WorkDay]

  def findByProjectSupervisorIdAndDateBetween(supervisorId: Long, from: LocalDate, to: LocalDate): util.Set[WorkDay]

  def findByAuthorIdAndProjectSupervisorIdAndDateBetween(authorId: Long, supervisorId: Long, from: LocalDate, to: LocalDate): util.Set[WorkDay]

  def findByAuthorIdAndDateBetween(authorId: Long, from: LocalDate, to: LocalDate): util.Set[WorkDay]

  def findByAuthorIdAndCheckedFalse(authorId: Long): util.Set[WorkDay]

  def findByProjectSupervisorIdAndCheckedFalse(supervisorId: Long): util.Set[WorkDay]

  def findByCheckedFalse: util.Set[WorkDay]

  def findByDateBetween(from: LocalDate, to: LocalDate): util.Set[WorkDay]

  def findByAuthorAndProjectAndDate(author: User, project: Project, localDate: LocalDate): WorkDay

  @Query(value = "SELECT sum(work_seconds) FROM work_day " +
                 "WHERE project_id = :projectId and author_id = :userId", nativeQuery = true)
  def getTotalWorkSeconds(@Param("userId") userId: Long, @Param("projectId") projectId: Long): Integer

  @Query(value = "SELECT sum(idle_seconds) FROM work_day " +
                 "WHERE project_id = :projectId AND author_id = :userId AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
  def getCurrentMonthIdleSeconds(@Param("userId") userId: Long,
                                 @Param("projectId") projectId: Long,
                                 @Param("startDate") startDate: LocalDate,
                                 @Param("endDate") endDate: LocalDate): Integer

  @Query(value = "SELECT sum(idle_seconds) FROM work_day " +
                 "WHERE project_id = :projectId AND author_id = :userId", nativeQuery = true)
  def getTotalIdleSeconds(@Param("userId") userId: Long, @Param("projectId") projectId: Long): Integer

  @Query(value = "SELECT sum(work_seconds) FROM work_day " +
                 "WHERE author_id = :userId AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
  def getTotalWorkSeconds(@Param("userId") userId: Long,
                          @Param("startDate") startDate: LocalDate,
                          @Param("endDate") endDate: LocalDate): Integer

  @Query(value = "SELECT sum(idle_seconds) FROM work_day " +
                 "WHERE author_id = :userId AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
  def getTotalIdleSeconds(@Param("userId") userId: Long,
                          @Param("startDate") startDate: LocalDate,
                          @Param("endDate") endDate: LocalDate): Integer

  @Query(value = "SELECT sum(symbols_count) FROM work_day " +
                 "WHERE author_id = :userId AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
  def getSymbolsCount(@Param("userId") userId: Long,
                      @Param("startDate") startDate: LocalDate,
                      @Param("endDate") endDate: LocalDate): Integer

  @Query(value = "SELECT sum(windows_switched_count) FROM work_day " +
                 "WHERE author_id = :userId AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
  def getWindowsSwitchedCount(@Param("userId") userId: Long,
                              @Param("startDate") startDate: LocalDate,
                              @Param("endDate") endDate: LocalDate): Integer

}
