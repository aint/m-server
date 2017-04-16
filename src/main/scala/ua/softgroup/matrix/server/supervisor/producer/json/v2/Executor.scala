package ua.softgroup.matrix.server.supervisor.producer.json.v2

import java.time.LocalTime

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class Executor {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  @BeanProperty
  var dayWorkTimeSeconds: Int = _

  @BeanProperty
  var idleTimeSeconds: Int = _

  @BeanProperty
  var idlePercentage: Double = _

  @BeanProperty
  var report: Report = _

  def this(id: Long, start: LocalTime, end: LocalTime, dayWorkTimeSeconds: Int,
           idleTimeSeconds: Int, idlePercentage: Double, report: Report) {
    this()
    this.id = id
    this.start = start
    this.end = end
    this.dayWorkTimeSeconds = dayWorkTimeSeconds
    this.idleTimeSeconds = idleTimeSeconds
    this.idlePercentage = idlePercentage
    this.report = report
  }

  override def toString: String = {
    "Executor(id=%d, start=%s, end=%s, dayWorkTimeSeconds=%d, idleTimeSeconds=%d, idlePercentage=%f, report=%s)"
      .format(id, start, end, dayWorkTimeSeconds, idleTimeSeconds, idlePercentage, report)
  }

}
