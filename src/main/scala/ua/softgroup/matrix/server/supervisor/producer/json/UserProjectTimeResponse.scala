package ua.softgroup.matrix.server.supervisor.producer.json

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserProjectTimeResponse extends TimeResponse {

  @BeanProperty
  var entityId: Long = _

  @BeanProperty
  var entityType: String = "project"

  def this(entityId: Long, workSeconds: Int, idleSeconds: Int, idlePercentage: Double) {
    this()
    this.entityId = entityId
    this.totalWorkTimeSeconds = workSeconds
    this.totalIdleTimeSeconds = idleSeconds
    this.totalIdlePercentage = idlePercentage
  }

  override def toString: String = {
    "UserProjectTimeResponse(entityId=%d, totalWorkTimeSeconds=%d, totalIdleTimeSeconds=%d, totalIdlePercentage=%f)"
      .format(entityId, totalWorkTimeSeconds, totalIdleTimeSeconds, totalIdlePercentage)
  }
}
