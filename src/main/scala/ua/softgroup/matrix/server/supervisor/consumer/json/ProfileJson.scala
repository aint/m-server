package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ProfileJson(@JsonProperty("first_name")
                  var firstName: String,

                  @JsonProperty("last_name")
                  var lastName: String,

                  @JsonProperty("middle_name")
                  var middleName: String,

                  @JsonProperty("monthly_rate")
                  var monthlyRate: Int,

                  @JsonProperty("monthly_rate_currency_id")
                  var monthlyRateCurrencyId: Int,

                  @JsonProperty("external_hourly_rate")
                  var externalHourlyRate: Int,

                  @JsonProperty("external_hourly_rate_currency_id")
                  var externalHourlyRateCurrencyId: Int,

                  @JsonProperty("internal_hourly_rate")
                  var internalHourlyRate: Int,

                  @JsonProperty("internal_hourly_rate_currency_id")
                  var internalHourlyRateCurrencyId: Int,

                  @JsonProperty("email_home")
                  var emailHome: String)
