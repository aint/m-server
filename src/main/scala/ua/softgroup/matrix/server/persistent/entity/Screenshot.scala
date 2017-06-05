package ua.softgroup.matrix.server.persistent.entity

import java.time.LocalDateTime
import javax.persistence.{Column, Entity, ManyToOne}

import org.springframework.data.jpa.domain.AbstractPersistable

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(-5537683353413610686L)
class Screenshot extends AbstractPersistable[java.lang.Long] {

  @Column
  @BeanProperty
  var creationTime: LocalDateTime = _

  @Column(columnDefinition = "text")
  @BeanProperty
  var screenshotTitle: String = _

  @Column
  @BeanProperty
  var path: String = _

  @ManyToOne
  @BeanProperty
  var trackingData: TrackingData = _

  def this(creationTime: LocalDateTime, screenshotTitle: String, path: String, trackingData: TrackingData) {
    this()
    this.creationTime = creationTime
    this.screenshotTitle = screenshotTitle
    this.path = path
    this.trackingData = trackingData
  }

  override def toString = s"Screenshot(id=${super.getId}, creationTime=$creationTime, screenshotTitle=$screenshotTitle, path=$path)"

}
