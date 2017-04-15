package ua.softgroup.matrix.server.persistent.entity

import java.util
import javax.persistence._

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(-6554909949520971201L)
class TrackingData extends AbstractEntity[java.lang.Long] {

  @Column(columnDefinition = "longtext")
  @BeanProperty
  var keyboardText: String = ""

  @Column
  @BeanProperty
  var mouseFootage: Double = _

  @OneToMany(mappedBy = "trackingData", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var screenshots: util.Set[Screenshot] = new util.HashSet[Screenshot]

  @OneToMany(mappedBy = "trackingData", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var activeWindows: util.List[WindowTime] = new util.ArrayList[WindowTime]

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_period_id")
  @BeanProperty
  var workTimePeriod: WorkTimePeriod = _

  def this(workTimePeriod: WorkTimePeriod) = {
    this()
    this.workTimePeriod = workTimePeriod
  }

  override def toString = s"TrackingData(id=${super.getId} keyboardText=${keyboardText.length}, mouseFootage=$mouseFootage)"

}
