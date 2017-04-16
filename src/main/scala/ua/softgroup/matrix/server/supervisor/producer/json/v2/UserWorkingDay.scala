package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.{LocalDate, LocalTime}
import java.util
import java.util.Set

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserWorkingDay {

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  @BeanProperty
  var totalWorkTimeSeconds: Int = _

  @BeanProperty
  var totalIdleTimeSeconds: Int = _

  @BeanProperty
  var totalIdlePercentage: Double = _

  @BeanProperty
  var periods: util.Set[Period] = _

  @BeanProperty
  var reports: util.Set[Report] = _

  def this(date: LocalDate, start: LocalTime, end: LocalTime, totalWorkTimeSeconds: Int,
           totalIdleTimeSeconds: Int, totalIdlePercentage: Double,
           periods: util.Set[Period], reports: util.Set[Report]) {
    this()
    this.date = date
    this.start = start
    this.end = end
    this.totalWorkTimeSeconds = totalWorkTimeSeconds
    this.totalIdleTimeSeconds = totalIdleTimeSeconds
    this.totalIdlePercentage = totalIdlePercentage
    this.periods = periods
    this.reports = reports
  }

  override def toString: String = {
    "UserWorkingDay(date=%s, start=%s, end=%s, totalWorkTimeSeconds=%d, totalIdleTimeSeconds=%d, totalIdlePercentage=%f, periods=%s, reports=%s)"
      .format(date, start, end, totalWorkTimeSeconds, totalIdleTimeSeconds, totalIdlePercentage, periods, reports)
  }

}
