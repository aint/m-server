package ua.softgroup.matrix.server.supervisor.consumer.json

import java.util

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ActiveProjectsJson {

  @JsonProperty
  @BooleanBeanProperty
  var success: Boolean = _

  @JsonProperty
  @BeanProperty
  var list: util.List[ProjectJson] = _

  override def toString = s"ActiveProjectsJson(success=$success, list=$list)"

}
