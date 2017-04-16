package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.LocalDate

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ReportResponse {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var entityType: String = "project"

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var authorId: Long = _

  @BeanProperty
  var entityId: Long = _

  @BeanProperty
  var checkedById: Long = _

  @BooleanBeanProperty
  var checked: Boolean = _

  @BeanProperty
  var coefficient: Double = _

  @BeanProperty
  var text: String = _

  @BeanProperty
  var dayWorkTimeSeconds: Int = _

  @BeanProperty
  var rate: Int = _

  @BeanProperty
  var currencyId: Int = _

  def this(id: Long, date: LocalDate, authorId: Long, entityId: Long, checkedById: Long, checked: Boolean,
           coefficient: Double, text: String, dayWorkTimeSeconds: Int, rate: Int, currencyId: Int) {
    this()
    this.id = id
    this.date = date
    this.authorId = authorId
    this.entityId = entityId
    this.checkedById = checkedById
    this.checked = checked
    this.coefficient = coefficient
    this.text = text
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.rate = rate
    this.currencyId = currencyId
  }

  override def toString: String = {
    "ReportResponse(id=" + id +
      ", entityType=" + entityType +
      ", date=" + date +
      ", authorId=" + authorId +
      ", entityId=" + entityId +
      ", checkedById=" + checkedById +
      ", checked=" + checked +
      ", coefficient=" + coefficient +
      ", text=" + text +
      ", dayWorkTimeSeconds=" + dayWorkTimeSeconds +
      ", rate=" + rate +
      ", currencyId=" + currencyId + ")"
  }

}
