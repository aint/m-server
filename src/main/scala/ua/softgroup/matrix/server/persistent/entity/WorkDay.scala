package ua.softgroup.matrix.server.persistent.entity

import java.time.{LocalDate, LocalDateTime}
import javax.persistence._
import java.util

import org.hibernate.annotations.CreationTimestamp

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(-5318207364986821484L)
class WorkDay extends AbstractEntity[java.lang.Long] {

  @Column
  @CreationTimestamp
  @BeanProperty
  var date: LocalDate = _

  @Column
  @BeanProperty
  var workSeconds: Int = _

  @Column
  @BeanProperty
  var idleSeconds: Int = _

  @Column(columnDefinition = "TEXT")
  @BeanProperty
  var reportText: String = ""

  @ManyToOne
  @BeanProperty
  var author: User = _

  @Column
  @BeanProperty
  var reportUpdated: LocalDateTime = _

  @Column
  @BeanProperty
  var checked: Boolean = _

  @Column
  @BeanProperty
  var jailerId: Long = _

  @Column
  @BeanProperty
  var coefficient: Double = 1.0

  @Column
  @BeanProperty
  var symbolsCount: Int = _

  @Column
  @BeanProperty
  var windowsSwitchedCount: Int = _

  @Column
  @BeanProperty
  var rate: Int = _

  @Column
  @BeanProperty
  var currencyId: Int = _

  @ManyToOne
  @BeanProperty
  var project: Project = _

  @OneToMany(mappedBy = "workDay", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var workTimePeriods: util.Set[WorkTimePeriod] = new util.HashSet[WorkTimePeriod]

  def this(workSeconds: Integer, idleSeconds: Integer) {
    this()
    this.workSeconds = workSeconds
    this.idleSeconds = idleSeconds
  }

  def this(author: User, project: Project, date: LocalDate) {
    this()
    this.author = author
    this.project = project
    this.date = date
  }

  override def toString: String = {
    s"WorkDay(id=%s, date=%s, workSeconds=%d, idleSeconds=%d, reportText=%s, checked=%b, jailerId=%d, coefficient=%f, symbolsCount=%d, windowsSwitchedCount=%d, rate=%d, currencyId=%d)"
      .format(super.getId, date, workSeconds, idleSeconds, reportText, checked, jailerId, coefficient, symbolsCount, windowsSwitchedCount, rate, currencyId)
  }

}

