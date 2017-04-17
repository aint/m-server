package ua.softgroup.matrix.server.persistent.repository

import org.springframework.data.repository.CrudRepository
import ua.softgroup.matrix.server.persistent.entity.{TrackingData, WorkDay}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait TrackingDataRepository extends CrudRepository[TrackingData, java.lang.Long] {

  def findByWorkTimePeriodWorkDay(workDay: WorkDay): TrackingData

}
