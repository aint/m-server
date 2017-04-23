package ua.softgroup.matrix.server.supervisor.consumer.json

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ProfileJson {

  @JsonProperty("first_name")
  @BeanProperty
  var firstName: String = _

  @JsonProperty("last_name")
  @BeanProperty
  var lastName: String = _

  @JsonProperty("middle_name")
  @BeanProperty
  var middleName: String = _

  @JsonProperty("monthly_rate")
  @BeanProperty
  var monthlyRate: Int = _

  @JsonProperty("monthly_rate_currency_id")
  @BeanProperty
  var monthlyRateCurrencyId: Int = _

  @JsonProperty("external_hourly_rate")
  @BeanProperty
  var externalHourlyRate: Int = _

  @JsonProperty("external_hourly_rate_currency_id")
  @BeanProperty
  var externalHourlyRateCurrencyId: Int = _

  @JsonProperty("internal_hourly_rate")
  @BeanProperty
  var internalHourlyRate: Int = _

  @JsonProperty("internal_hourly_rate_currency_id")
  @BeanProperty
  var internalHourlyRateCurrencyId: Int = _

  @JsonProperty("email_home")
  @BeanProperty
  var emailHome: String = _

  override def toString: String = "ProfileJson(" +
    "firstName=" + firstName +
    ", lastName=" + lastName +
    ", middleName=" + middleName +
    ", monthlyRate=" + monthlyRate +
    ", monthlyRateCurrencyId=" + monthlyRateCurrencyId +
    ", externalHourlyRate=" + externalHourlyRate +
    ", externalHourlyRateCurrencyId=" + externalHourlyRateCurrencyId +
    ", internalHourlyRate=" + internalHourlyRate +
    ", internalHourlyRateCurrencyId=" + internalHourlyRateCurrencyId +
    ", emailHome=" + emailHome + ")"

}
