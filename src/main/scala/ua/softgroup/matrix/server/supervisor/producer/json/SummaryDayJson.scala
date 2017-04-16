package ua.softgroup.matrix.server.supervisor.producer.json

import java.time.LocalDate
import java.util

import ua.softgroup.matrix.server.supervisor.producer.json.v2.DayJson

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class SummaryDayJson {

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var totalWorkSeconds: Int = _

  @BeanProperty
  var totalIdleSeconds: Int = _

  @BeanProperty
  var totalIdlePercentage: Double = _

  @BeanProperty
  var workDays: util.Set[DayJson] = _

  override def toString: String = {
    "SummaryDayJson(date=%s, totalWorkSeconds=%d, totalIdleSeconds=%d, totalIdlePercentage=%f, workDays=%s)"
      .format(date, totalWorkSeconds, totalIdleSeconds, totalIdlePercentage, workDays)
  }

}
