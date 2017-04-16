package ua.softgroup.matrix.server.supervisor.producer.json

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserTimeResponse extends TimeResponse {

  @BeanProperty
  var userId: Long = _

  def this(userId: Long, workSeconds: Int, idleSeconds: Int, idlePercentage: Double) {
    this()
    this.userId = userId
    this.totalWorkTimeSeconds = workSeconds
    this.totalIdleTimeSeconds = idleSeconds
    this.totalIdlePercentage = idlePercentage
  }

  override def toString: String = {
    "UserTimeResponse(userId=%d, totalWorkTimeSeconds=%d, totalIdleTimeSeconds=%d, totalIdlePercentage=%f)"
      .format(userId, totalWorkTimeSeconds, totalIdleTimeSeconds, totalIdlePercentage)
  }

}
