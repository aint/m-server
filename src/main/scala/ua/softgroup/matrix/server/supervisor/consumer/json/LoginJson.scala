package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class LoginJson(var success: Boolean, var message: String,
                @JsonProperty("tracker-token") var trackerToken: String,
                var user: UserJson)
