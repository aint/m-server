package ua.softgroup.matrix.server.persistent.entity

import java.time.LocalTime
import javax.persistence._

import org.springframework.data.jpa.domain.AbstractPersistable

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(2140610419978157701L)
class WorkTimePeriod extends AbstractPersistable[java.lang.Long] {

  @Column
  @BeanProperty
  var start: LocalTime = _

  @Column
  @BeanProperty
  var end: LocalTime = _

  @OneToOne(mappedBy = "workTimePeriod", cascade = Array(CascadeType.ALL), orphanRemoval = true, fetch = FetchType.LAZY)
  @BeanProperty
  var trackingData: TrackingData = _

  @ManyToOne
  @BeanProperty
  var workDay: WorkDay = _

  def this(start: LocalTime, end: LocalTime, workDay: WorkDay) {
    this()
    this.start = start
    this.end = end
    this.workDay= workDay
  }

  def this(start: LocalTime, end: LocalTime, trackingData: TrackingData, workDay: WorkDay) {
    this()
    this.start = start
    this.end = end
    this.trackingData= trackingData
    this.workDay= workDay
  }

  override def toString = s"WorkTimePeriod(id=${super.getId}, start=$start, end=$end)"
}
