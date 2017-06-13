package ua.softgroup.matrix.server.supervisor.consumer.json

import java.util

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class SettingsJson {

  @JsonProperty
  @BooleanBeanProperty
  var success: Boolean = _

  @JsonProperty
  @BeanProperty
  var list: List[SettingJson] = _

  override def toString = s"SettingsJson(success=$success, list=$list)"

}
