package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class UserJson(var id: Long,

               var email: String,

               var username: String,

               @JsonProperty("tracker_token")
               var trackerToken: String,

               var profile: ProfileJson)
