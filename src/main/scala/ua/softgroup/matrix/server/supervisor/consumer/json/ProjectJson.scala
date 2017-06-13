package ua.softgroup.matrix.server.supervisor.consumer.json

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ProjectJson(var id: Long,

                  var title: String,

                  @JsonProperty("description_text")
                  var description: String,

                  @JsonProperty("author_name")
                  var authorName: String,

                  @JsonProperty("start_date")
                  var startDate: LocalDate,

                  @JsonProperty("end_date")
                  var endDate: LocalDate,

                  var rate: Int,

                  @JsonProperty("rate_currency_id")
                  var rateCurrencyId: Int)
