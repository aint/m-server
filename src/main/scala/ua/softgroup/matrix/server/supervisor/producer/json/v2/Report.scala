package ua.softgroup.matrix.server.supervisor.producer.json.v2

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class Report {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var entityId: Long = _

  @BeanProperty
  var entityType: String = "project"

  @BooleanBeanProperty
  var checked: Boolean = _

  @BeanProperty
  var checkedById: Long = _

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

  def this(id: Long, entityId: Long, checked: Boolean, checkedById: Long,
           coefficient: Double, text: String, dayWorkTimeSeconds: Int, rate: Int, currencyId: Int) {
    this()
    this.id = id
    this.entityId = entityId
    this.checked = checked
    this.checkedById = checkedById
    this.coefficient = coefficient
    this.text = text
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.rate = rate
    this.currencyId = currencyId
  }

  override def toString: String = {
    "Report(id=%d, entityId=%d, entityType=%s, checked=%b, checkedById=%d, coefficient=%f, text=%s, dayWorkTimeSeconds=%d, rate=%d, currencyId=%d)"
      .format(id, entityId, entityType, checked, checkedById, coefficient, text, dayWorkTimeSeconds, rate, currencyId)
  }

}
