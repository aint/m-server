package ua.softgroup.matrix.server.supervisor.producer.json.tracking

import java.time.LocalTime

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class TrackingPeriodJson {

  @BeanProperty
  var start: LocalTime = _

  @BeanProperty
  var end: LocalTime = _

  @BeanProperty
  var windowName: String = _

  @BeanProperty
  var keyLogger: String = _

  @BeanProperty
  var screenshot: String = _

  def this(start: LocalTime, end: LocalTime, keyLogger: String, screenshotAndTitle: Array[String]) {
    this()
    this.start = start
    this.end = end
    this.keyLogger = keyLogger
    this.screenshot = screenshotAndTitle(0)
    this.windowName = screenshotAndTitle(1)
  }

  override def toString: String = {
    "TrackingPeriodJson(start=%s, end=%s, windowName=%s, keyLogger=%s, screenshot=%s)"
      .format(start, end, windowName, keyLogger, screenshot)
  }

}
