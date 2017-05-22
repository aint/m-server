package ua.softgroup.matrix.server.service.impl

import java.util.Optional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.server.persistent.entity.{WorkDay, WorkTimePeriod}
import ua.softgroup.matrix.server.persistent.repository.WorkTimePeriodRepository
import ua.softgroup.matrix.server.service.WorkTimePeriodService

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Service
class WorkTimePeriodServiceImpl @Autowired() (repository: WorkTimePeriodRepository)
  extends AbstractEntityTransactionalService[WorkTimePeriod] (repository) with WorkTimePeriodService {

  override def getLatestPeriodOf(workDay: WorkDay): Optional[WorkTimePeriod] =
    Optional.ofNullable(getRepository.findTopByWorkDayOrderByStartDesc(workDay))

  override protected def getRepository: WorkTimePeriodRepository = repository.asInstanceOf[WorkTimePeriodRepository]

}
