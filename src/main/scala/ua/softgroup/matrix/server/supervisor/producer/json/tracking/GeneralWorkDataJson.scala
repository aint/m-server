package ua.softgroup.matrix.server.supervisor.producer.json.tracking

import java.time.{LocalDate, LocalTime}

import com.fasterxml.jackson.annotation.JsonView

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class GeneralWorkDataJson {

  @JsonView(Array(classOf[TrackingDataViewType.USER]))
  @BeanProperty
  var userId: Long = _

  @JsonView(Array(classOf[TrackingDataViewType.PROJECT]))
  @BeanProperty
  var entityId: Long = _
  @JsonView(Array(classOf[TrackingDataViewType.PROJECT]))
  @BeanProperty
  var entityType: String = "project"

  @JsonView(Array(classOf[TrackingDataViewType.DATE]))
  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var start: LocalTime = _
  @BeanProperty
  var end: LocalTime = _
  @BeanProperty
  var dayWorkTimeSeconds: Int = _
  @BeanProperty
  var periods: List[TrackingPeriodJson] = _

  def this(date: LocalDate, start: LocalTime, end: LocalTime, dayWorkTimeSeconds: Int,
           periods: List[TrackingPeriodJson]) {
    this()
    this.date = date
    this.start = start
    this.end = end
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.periods = periods
  }

  def this(userId: Long, start: LocalTime, end: LocalTime, dayWorkTimeSeconds: Int,
           periods: List[TrackingPeriodJson]) {
    this()
    this.userId = userId
    this.start = start
    this.end = end
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.periods = periods
  }

  def this(entityId: Long, entityType: String, start: LocalTime, end: LocalTime,
           dayWorkTimeSeconds: Int, periods: List[TrackingPeriodJson]) {
    this()
    this.entityId = entityId
//    this.entityType = entityType
    this.start = start
    this.end = end
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.periods = periods
  }

  override def toString: String = {
    "GeneralWorkDataJson(userId=%d, entityId=%d, entityType=%s, date=%s, start=%s, end=%s, dayWorkTimeSeconds=%d, periods=%s)"
      .format(userId, entityId, entityType, date, start, end, dayWorkTimeSeconds, periods)
  }

}
