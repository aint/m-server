package ua.softgroup.matrix.server.persistent.repository

import java.util

import org.springframework.data.repository.CrudRepository
import ua.softgroup.matrix.server.persistent.entity.{Project, User}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait ProjectRepository extends CrudRepository[Project, java.lang.Long] {

  def findBySupervisorIdAndUser(supervisorId: Long, user: User): Project

  def findByUser(user: User): util.List[Project]

  def findByUserId(userId: Long): util.Set[Project]

  def findBySupervisorId(supervisorId: Long): util.Set[Project]

}
