package ua.softgroup.matrix.server.supervisor.consumer.endpoint

import retrofit2.Call
import retrofit2.http.{Field, FormUrlEncoded, Header, POST}
import ua.softgroup.matrix.server.supervisor.consumer.json.{ActiveProjectsJson, CurrenciesJson, LoginJson, SettingsJson}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
trait SupervisorEndpoint {

  @FormUrlEncoded
  @POST("auth/login")
  def login(@Field("login") login: String, @Field("pass") password: String): Call[LoginJson]

  @POST("project/get-user-active-projects")
  def getUserActiveProjects(@Header("tracker-token") trackerToken: String): Call[ActiveProjectsJson]

  @POST("currency/get-currencies")
  def getCurrencies(@Header("tracker-token") trackerToken: String): Call[CurrenciesJson]

  @POST("setting/get-tracker-settings")
  def getTrackerSettings(@Header("tracker-token") trackerToken: String): Call[SettingsJson]

}
