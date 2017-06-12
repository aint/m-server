package ua.softgroup.matrix.server.service

import java.util
import java.util.Optional

import ua.softgroup.matrix.api.model.datamodels.{ProjectModel, TimeModel}
import ua.softgroup.matrix.server.persistent.entity.{Project, User}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait ProjectService extends GenericEntityService[Project] {

  def saveStartWorkTime(userToken: String, projectId: Long): TimeModel

  def saveEndWorkTime(userToken: String, projectId: Long): TimeModel

  def saveCheckpointTime(userToken: String, projectId: Long, idleTime: Int): TimeModel

  def getUserActiveProjects(token: String): util.Set[ProjectModel]

  def getUserActiveProjects(userId: Long): Set[Project]

  def getBySupervisorIdAndUser(supervisorId: Long, user: User): Optional[Project]

  def getBySupervisorId(supervisorId: Long): Set[Project]

}
