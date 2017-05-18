package ua.softgroup.matrix.server.supervisor.producer.json.time

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
class TimeManagement {

  @BeanProperty
  var entityId: Long = _

  @BeanProperty
  var entityType: String = _

  @BeanProperty
  var userId: Long = _

  @BeanProperty
  var onDate: String = _

  @BeanProperty
  var time: Int = _

  @BeanProperty
  var idleAction: Int = _

  @BeanProperty
  var action: String = _

  @BeanProperty
  var reason: String = _

  override def toString: String = {
    "TimeManagment(entityId=%d, entityType=%s, userId=%d, onDate=%s, time=%d, idleAction=%d, action=%s, reason=%s)"
      .format(entityId, entityType, userId, onDate, time, idleAction, action, reason)
  }

}
