package ua.softgroup.matrix.server.supervisor.consumer.json

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ProjectJson {

  @JsonProperty
  @BeanProperty
  var id: Long = _

  @JsonProperty
  @BeanProperty
  var title: String = _

  @JsonProperty("description_text")
  @BeanProperty
  var description: String = _

  @JsonProperty("author_name")
  @BeanProperty
  var authorName: String = _

  @JsonProperty("start_date")
  @BeanProperty
  var startDate: LocalDate = _

  @JsonProperty("end_date")
  @BeanProperty
  var endDate: LocalDate = _

  @JsonProperty
  @BeanProperty
  var rate: Int = _

  @JsonProperty("rate_currency_id")
  @BeanProperty
  var rateCurrencyId: Int = _

  override def toString: String = {
    "ProjectJson(id=%d, title=%s, description=%s, authorName=%s, startDate=%s, endDate=%s, rate=%d, rateCurrencyId=%d)"
      .format(id, title, description, authorName, startDate, endDate, rate, rateCurrencyId)
  }

}
