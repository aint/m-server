package ua.softgroup.matrix.server.persistent.entity

import java.time.LocalDateTime
import javax.persistence.{Column, Entity, Lob, ManyToOne}

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(-5537683353413610686L)
class Screenshot extends AbstractEntity[java.lang.Long] {

  @Lob
  @BeanProperty
  var imageBytes: Array[Byte] = _

  @Column
  @BeanProperty
  var creationTime: LocalDateTime = _

  @Column(columnDefinition = "text")
  @BeanProperty
  var screenshotTitle: String = _

  @ManyToOne
  @BeanProperty
  var trackingData: TrackingData = _

  def this(imageBytes: Array[Byte], creationTime: LocalDateTime, screenshotTitle: String, trackingData: TrackingData) {
    this()
    this.imageBytes = imageBytes
    this.creationTime = creationTime
    this.screenshotTitle = screenshotTitle
    this.trackingData = trackingData
  }

  override def toString = s"Screenshot(id=${super.getId}, creationTime=$creationTime, screenshotTitle=$screenshotTitle)"

}
