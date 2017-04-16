package ua.softgroup.matrix.server.supervisor.producer.json.tracking

import java.time.LocalDate
import java.util

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class TrackingDataJson {

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var workData: util.List[GeneralWorkDataJson] = _

  def this(date: LocalDate, workData: util.List[GeneralWorkDataJson]) {
    this()
    this.date = date
    this.workData = workData
  }

  override def toString = s"TrackingDataJson(date=$date, workData=$workData)"

}
