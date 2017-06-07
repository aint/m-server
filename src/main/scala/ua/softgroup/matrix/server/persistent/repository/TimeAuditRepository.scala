package ua.softgroup.matrix.server.persistent.repository

import java.time.LocalDateTime
import java.util

import org.springframework.data.repository.CrudRepository
import ua.softgroup.matrix.server.persistent.entity.{TimeAudit, User}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait TimeAuditRepository extends CrudRepository[TimeAudit, java.lang.Long] {

  def findByPrincipalId(principalId: Long): util.Set[TimeAudit]

  def findByCreationDateBetween(start: LocalDateTime, end: LocalDateTime): util.Set[TimeAudit]

}
