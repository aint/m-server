package ua.softgroup.matrix.server.supervisor.producer.json

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
abstract class TimeResponse {

  @BeanProperty
  protected var totalWorkTimeSeconds: Int = _

  @BeanProperty
  protected var totalIdleTimeSeconds: Int = _

  @BeanProperty
  protected var totalIdlePercentage: Double = _

}
