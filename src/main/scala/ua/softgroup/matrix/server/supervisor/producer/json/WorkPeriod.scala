package ua.softgroup.matrix.server.supervisor.producer.json

import java.time.LocalTime

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class WorkPeriod {

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  def this(start: LocalTime, end: LocalTime) {
    this()
    this.start = start
    this.end = end
  }

  override def toString = s"WorkPeriod(start=$start, end=$end)"

}
