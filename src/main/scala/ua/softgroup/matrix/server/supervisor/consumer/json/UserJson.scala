package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserJson {

  @JsonProperty
  @BeanProperty
  var id: Long = _

  @JsonProperty
  @BeanProperty
  var email: String = _

  @JsonProperty
  @BeanProperty
  var username: String = _

  @JsonProperty("tracker_token")
  @BeanProperty
  var trackerToken: String = _

  @JsonProperty
  @BeanProperty
  var profile: ProfileJson = _

  override def toString = s"UserJson(id=$id, email=$email, username=$username, trackerToken=$trackerToken, profile=$profile)"

}
