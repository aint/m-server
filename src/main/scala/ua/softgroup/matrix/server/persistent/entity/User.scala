package ua.softgroup.matrix.server.persistent.entity

import javax.persistence.{Column, Entity, Id}

import scala.beans.BeanProperty

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Entity
@SerialVersionUID(1L)
class User {

  @Id
  @BeanProperty
  var id: Long = _

  @Column
  @BeanProperty
  var trackerToken: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var username: String = _

  @Column(nullable = false)
  @BeanProperty
  var password: String = _

  @Column
  @BeanProperty
  var externalHourlyRate: Integer = _

  @Column
  @BeanProperty
  var externalHourlyRateCurrencyId: Integer = _

  @Column
  @BeanProperty
  var internalHourlyRate: Integer = _

  @Column
  @BeanProperty
  var internalHourlyRateCurrencyId: Integer = _

  override def toString: String = {
    "User(id=%d, trackerToken=%s, username=%s, password=%s, externalHourlyRate=%s, externalHourlyRateCurrencyId=%s, internalHourlyRate=%s, internalHourlyRateCurrencyId=%s)"
      .format(id, trackerToken, username, password, externalHourlyRate, externalHourlyRateCurrencyId, internalHourlyRate, internalHourlyRateCurrencyId)
  }

}
