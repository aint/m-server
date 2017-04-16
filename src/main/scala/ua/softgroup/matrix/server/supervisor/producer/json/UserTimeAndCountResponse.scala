package ua.softgroup.matrix.server.supervisor.producer.json

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserTimeAndCountResponse extends TimeResponse {

  @BeanProperty
  var userId: Long = _

  @BeanProperty
  var totalAllSymbolsCount: Int = _

  @BeanProperty
  var windowsSwitchedCount: Int = _

  def this(userId: Long, workSeconds: Int, idleSeconds: Int, idlePercentage: Double,
           totalAllSymbolsCount: Int, windowsSwitchedCount: Int) {
    this()
    this.userId = userId
    this.totalWorkTimeSeconds = workSeconds
    this.totalIdleTimeSeconds = idleSeconds
    this.totalIdlePercentage = idlePercentage
    this.totalAllSymbolsCount = totalAllSymbolsCount
    this.windowsSwitchedCount = windowsSwitchedCount
  }

  override def toString: String = {
    "UserTimeAndCountResponse(userId=%d, totalWorkTimeSeconds=%d, totalIdleTimeSeconds=%d, totalIdlePercentage=%f, totalAllSymbolsCount=%d, windowsSwitchedCount=%d)"
      .format(userId, totalWorkTimeSeconds, totalIdleTimeSeconds, totalIdlePercentage, totalAllSymbolsCount, windowsSwitchedCount)
  }

}
