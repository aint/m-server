package ua.softgroup.matrix.server.persistent.entity

import java.time.{LocalDate, LocalDateTime}
import java.util
import javax.persistence._

import org.springframework.data.jpa.domain.AbstractPersistable

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(1L)
class Project extends AbstractPersistable[java.lang.Long] {

  @Column
  @BeanProperty
  var supervisorId: Long = _

  @Column(columnDefinition = "text")
  @BeanProperty
  var title: String = _

  @Column
  @BeanProperty
  var description: String = _

  @Column
  @BeanProperty
  var authorName: String = _

  @Column
  @BeanProperty
  var startDate: LocalDate = _

  @Column
  @BeanProperty
  var endDate: LocalDate = _

  @Column
  @BeanProperty
  var rate: Integer = _

  @Column
  @BeanProperty
  var rateCurrencyId: Integer = _

  @Column
  @BeanProperty
  var workStarted: LocalDateTime = _

  @Column
  @BeanProperty
  var checkpointTime: LocalDateTime = _

  @ManyToOne
  @BeanProperty
  var user: User = _

  @OneToMany(mappedBy = "project", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  var workDays: util.Set[WorkDay] = new util.HashSet[WorkDay]

  def this(id: Long) {
    this()
    super.setId(id)
  }

  override def toString: String = {
    "Project(id=%s, supervisorId=%d, title=%s, description=%s, authorName=%s, startDate=%s, endDate=%s, rate=%s, rateCurrencyId=%s, workStarted=%s, checkpointTime=%s)"
      .format(super.getId, supervisorId, title, description, authorName, startDate, endDate, rate, rateCurrencyId, workStarted, checkpointTime)
  }
}
