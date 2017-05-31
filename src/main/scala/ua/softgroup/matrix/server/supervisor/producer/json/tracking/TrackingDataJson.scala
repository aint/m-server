package ua.softgroup.matrix.server.supervisor.producer.json.tracking

import java.time.LocalDate

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class TrackingDataJson {

  @BeanProperty
  var date: LocalDate = _

  @BeanProperty
  var workData: List[GeneralWorkDataJson] = _

  def this(date: LocalDate, workData: List[GeneralWorkDataJson]) {
    this()
    this.date = date
    this.workData = workData
  }

  override def toString = s"TrackingDataJson(date=$date, workData=$workData)"

}
