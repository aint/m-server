package ua.softgroup.matrix.server.persistent.entity

import java.time.LocalDateTime
import javax.persistence.{Column, Entity, OneToOne}

import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.domain.AbstractPersistable

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(7093407748878141348L)
class TimeAudit extends AbstractPersistable[java.lang.Long] {

  @Column
  @CreationTimestamp
  @BeanProperty
  var creationDate: LocalDateTime = _

  @Column
  @BeanProperty
  var addedMinutes: Int = _

  @Column
  @BeanProperty
  var reason: String = _

  @OneToOne
  @BeanProperty
  var adder: User = _

  @OneToOne
  @BeanProperty
  var workDay: WorkDay = _

  def this(addedMinutes: Int, reason: String, adder: User, workDay: WorkDay) {
    this()
    this.addedMinutes = addedMinutes
    this.reason = reason
    this.adder = adder
    this.workDay = workDay
  }


  override def toString = s"TimeAudit(${super.getId}, creationDate=$creationDate, addedMinutes=$addedMinutes, reason=$reason)"
}
