package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.LocalTime

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class Period {

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  @BeanProperty
  var workTimeSeconds: Int = _

  @BeanProperty
  var idleTimeSeconds: Int = _

  @BeanProperty
  var idlePercentage: Double = _

  @BeanProperty
  var entityId: Long = _

  @BeanProperty
  var entityType: String = "project"

  @BeanProperty
  var rate: Int = _

  @BeanProperty
  var currencyId: Int = _

  def this(start: LocalTime, end: LocalTime, workTimeSeconds: Int, idleTimeSeconds: Int,
           idlePercentage: Double, entityId: Long, rate: Int, currencyId: Int) {
    this()
    this.start = start
    this.end = end
    this.workTimeSeconds = workTimeSeconds
    this.idleTimeSeconds = idleTimeSeconds
    this.idlePercentage = idlePercentage
    this.entityId = entityId
    this.rate = rate
    this.currencyId = currencyId
  }

  override def toString: String = {
    "Period(start=%s, end=%s, workTimeSeconds=%d, idleTimeSeconds=%d, idlePercentage=%f, entityId=%d, entityType=%s, rate=%d, currencyId=%d)"
      .format(start, end, workTimeSeconds, idleTimeSeconds, idlePercentage, entityId, entityType, rate, currencyId)
  }

}
