package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.LocalDate
import java.util

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ProjectWorkingDay {

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var totalDayWorkTimeSeconds: Int = _

  @BeanProperty
  var totalIdleTimeSeconds: Int = _

  @BeanProperty
  var totalIdlePercentage: Double = _

  @BeanProperty
  var executors: util.Set[Executor] = _

  override def toString: String = {
    "ProjectWorkingDay(date=%s, totalDayWorkTimeSeconds=%d, totalIdleTimeSeconds=%d, totalIdlePercentage=%f, executors=%s)"
      .format(date, totalDayWorkTimeSeconds, totalIdleTimeSeconds, totalIdlePercentage, executors)
  }

}
