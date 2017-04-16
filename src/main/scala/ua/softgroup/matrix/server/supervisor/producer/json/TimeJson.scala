package ua.softgroup.matrix.server.supervisor.producer.json

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonView

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class TimeJson {

  @JsonView(Array(classOf[JsonViewType.OUT]))
  @BeanProperty
  var todayMinutes: Int = _

  @JsonView(Array(classOf[JsonViewType.OUT], classOf[JsonViewType.IN]))
  @BeanProperty
  var totalMinutes: Int = _

  @JsonView(Array(classOf[JsonViewType.IN]))
  @BeanProperty
  var reason: String = _

  @JsonView(Array(classOf[JsonViewType.IN]))
  @BeanProperty
  var date: LocalDate = _

  def this(todayMinutes: Integer, totalMinutes: Integer) {
    this()
    this.todayMinutes = todayMinutes
    this.totalMinutes = totalMinutes
  }

  override def toString = s"TimeJson(todayMinutes=$todayMinutes, totalMinutes=$totalMinutes, reason=$reason, date=$date)"

}
