package ua.softgroup.matrix.server.service

import ua.softgroup.matrix.server.persistent.entity.WorkDay

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
trait TimeAuditService {

  def save(timeSeconds: Int, reason: String, principalId: Long, workDay: WorkDay)

}
