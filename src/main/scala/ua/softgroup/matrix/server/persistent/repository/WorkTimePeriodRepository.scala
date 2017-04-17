package ua.softgroup.matrix.server.persistent.repository

import org.springframework.data.repository.CrudRepository
import ua.softgroup.matrix.server.persistent.entity.{WorkDay, WorkTimePeriod}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait WorkTimePeriodRepository extends CrudRepository[WorkTimePeriod, java.lang.Long] {

  def findTopByWorkDayOrderByStartAsc(workDay: WorkDay): WorkTimePeriod

  def findTopByWorkDayOrderByEndDesc(workDay: WorkDay): WorkTimePeriod

  def findTopByWorkDayOrderByStartDesc(workDay: WorkDay): WorkTimePeriod

}
