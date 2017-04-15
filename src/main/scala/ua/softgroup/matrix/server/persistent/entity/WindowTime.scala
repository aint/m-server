package ua.softgroup.matrix.server.persistent.entity

import java.time.LocalTime
import javax.persistence.{Column, Entity, ManyToOne}

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(-3216454660635129210L)
class WindowTime extends AbstractEntity[java.lang.Long] {

  @Column(columnDefinition = "TEXT")
  @BeanProperty
  var windowTitle: String = _

  @Column
  @BeanProperty
  var startTime: LocalTime = _

  @Column
  @BeanProperty
  var time: Int = _

  @ManyToOne
  @BeanProperty
  var trackingData: TrackingData = _

  def this(windowTitle: String, startTime: LocalTime, time: Int, trackingData: TrackingData) {
    this()
    this.windowTitle = windowTitle
    this.startTime = startTime
    this.time= time
    this.trackingData= trackingData
  }

  override def toString = s"WindowTime(id=${super.getId}, windowTitle=$windowTitle, startTime=$startTime, time=$time)"
}
