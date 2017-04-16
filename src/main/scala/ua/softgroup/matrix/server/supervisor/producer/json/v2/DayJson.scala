package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.{LocalDate, LocalTime}
import java.util

import ua.softgroup.matrix.server.supervisor.producer.json.WorkPeriod

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class DayJson {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var entityType: String = "project" //TODO: use enum

  @BeanProperty
  var projectId: Long = _

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  @BeanProperty
  var workSeconds: Int = _

  @BeanProperty
  var idleSeconds: Int = _

  @BeanProperty
  var idlePercentage: Double = _

  @BooleanBeanProperty
  var checked: Boolean = _

  @BeanProperty
  var checkerId: Long = _

  @BeanProperty
  var coefficient: Double = _

  @BeanProperty
  var reportText: String = ""

  @BeanProperty
  var rate: Int = _

  @BeanProperty
  var currencyId: Int = _

  @BeanProperty
  var workPeriods: util.Set[WorkPeriod] = _

  def this(id: Long, projectId: Long, date: LocalDate, start: LocalTime, end: LocalTime,
           workSeconds: Int, idleSeconds: Int, idlePercentage: Double, checked: Boolean,
           checkerId: Long, coefficient: Double, reportText: String, rate: Int, currencyId: Int,
           workPeriods: util.Set[WorkPeriod]) {
    this()
    this.id = id
    this.projectId = projectId
    this.date = date
    this.start = start
    this.end = end
    this.workSeconds = workSeconds
    this.idleSeconds = idleSeconds
    this.idlePercentage = idlePercentage
    this.checked = checked
    this.checkerId = checkerId
    this.coefficient = coefficient
    this.reportText = reportText
    this.rate = rate
    this.currencyId = currencyId
    this.workPeriods = workPeriods
  }

  override def toString: String = {
    "DayJson(id=" + id +
      ", entityType=" + entityType +
      ", projectId=" + projectId +
      ", date=" + date +
      ", start=" + start +
      ", end=" + end +
      ", workSeconds=" + workSeconds +
      ", idleSeconds=" + idleSeconds +
      ", idlePercentage=" + idlePercentage +
      ", checked=" + checked +
      ", checkerId=" + checkerId +
      ", coefficient=" + coefficient +
      ", reportText=" + reportText +
      ", rate=" + rate +
      ", currencyId=" + currencyId +
      ", workPeriods=" + workPeriods + ")"
  }

}
