package ua.softgroup.matrix.server.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.server.persistent.entity.{TimeAudit, WorkDay}
import ua.softgroup.matrix.server.persistent.repository.TimeAuditRepository
import ua.softgroup.matrix.server.service.TimeAuditService

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Service
class TimeAuditServiceImpl @Autowired() (repository: TimeAuditRepository) extends TimeAuditService {

  override def save(timeSeconds: Int, reason: String, principalId: Long, workDay: WorkDay): Unit = {
    repository.save(new TimeAudit(timeSeconds, reason, principalId, workDay))
  }

}
