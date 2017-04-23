package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class LoginJson {

  @JsonProperty
  @BeanProperty
  var success: Boolean = _

  @JsonProperty
  @BeanProperty
  var message: String = _

  @JsonProperty("tracker-token")
  @BeanProperty
  var trackerToken: String = _

  @JsonProperty
  @BeanProperty
  var user: UserJson = _

  override def toString = s"LoginJson(success=$success, message=$message, trackerToken=$trackerToken, user=$user)"

}
