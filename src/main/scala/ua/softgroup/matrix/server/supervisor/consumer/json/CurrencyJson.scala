package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class CurrencyJson {

  @JsonProperty
  @BeanProperty
  var id: Int = _

  @JsonProperty
  @BeanProperty
  var name: String = _

  override def toString = s"CurrencyJson(id=$id, name=$name)"

}
