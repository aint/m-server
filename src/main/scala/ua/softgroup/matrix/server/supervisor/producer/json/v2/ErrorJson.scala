package ua.softgroup.matrix.server.supervisor.producer.json.v2

import com.fasterxml.jackson.annotation.JsonView
import ua.softgroup.matrix.server.supervisor.producer.json.JsonViewType

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ErrorJson {

  @JsonView(Array(classOf[JsonViewType.OUT]))
  @BeanProperty
  var message: String = _

  def this(message: String) {
    this()
    this.message = message
  }

  override def toString = s"ErrorJson(message=$message)"

}
