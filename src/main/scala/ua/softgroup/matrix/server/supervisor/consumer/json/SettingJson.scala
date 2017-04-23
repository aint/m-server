package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class SettingJson {

  @JsonProperty
  @BeanProperty
  var key: String = _

  @JsonProperty
  @BeanProperty
  var value: Int = _

  override def toString = s"SettingJson(key=$key, value=$value)"

}
