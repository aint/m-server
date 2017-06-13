package ua.softgroup.matrix.server.service

import java.time.{LocalDate, LocalTime}
import java.util
import java.util.Optional

import ua.softgroup.matrix.api.model.datamodels.ReportModel
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus
import ua.softgroup.matrix.server.persistent.entity.{Project, User, WorkDay}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
trait WorkDayService extends GenericEntityService[WorkDay] {

  def getTotalWorkSeconds(author: User, project: Project): Int

  def getTotalWorkSeconds(author: User, date: LocalDate): Int

  def getCurrentMonthIdleSeconds(author: User, project: Project): Int

  def getTotalIdleSeconds(author: User, project: Project): Int

  def getTotalIdleSeconds(author: User, date: LocalDate): Int

  def getTotalWorkSeconds(userId: Long, from: LocalDate, to: LocalDate): Int

  def getTotalIdleSeconds(userId: Long, from: LocalDate, to: LocalDate): Int

  def getSymbolsCount(userId: Long, from: LocalDate, to: LocalDate): Int

  def getWindowsSwitchedCount(userId: Long, from: LocalDate, to: LocalDate): Int

  def getByAuthorAndProjectAndDate(author: User, project: Project, localDate: LocalDate): Optional[WorkDay]

  def getAllWorkDaysOf(userId: Long, projectSupervisorId: Long, from: LocalDate, to: LocalDate): Set[WorkDay]

  def getAllWorkDaysOf(user: User, localDate: LocalDate): util.Set[WorkDay]

  def getAllWorkDaysOf(projectSupervisorId: Long, date: LocalDate): util.Set[WorkDay]

  def getProjectWorkDaysBetween(projectSupervisorId: Long, from: LocalDate, to: LocalDate): Set[WorkDay]

  def getUserWorkDaysBetween(userId: Long, from: LocalDate, to: LocalDate): Set[WorkDay]

  def getUserNotCheckedWorkDays(userId: Long): Set[WorkDay]

  def getProjectNotCheckedWorkDays(projectSupervisorId: Long): Set[WorkDay]

  def getAllNotCheckedWorkDays: util.Set[WorkDay]

  def getWorkDaysBetween(from: LocalDate, to: LocalDate): Set[WorkDay]

  def getWorkDaysOf(userToken: String, projectId: Long): util.Set[ReportModel]

  def saveReportOrUpdate(userToken: String, projectId: Long, reportModel: ReportModel): ResponseStatus

  def getStartWorkOf(workDay: WorkDay): LocalTime

  def getEndWorkOf(workDay: WorkDay): LocalTime

}
