package ua.softgroup.matrix.server.persistent.repository

import org.springframework.data.repository.CrudRepository
import ua.softgroup.matrix.server.persistent.entity.User

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait UserRepository extends CrudRepository[User, java.lang.Long] {

  /**
    * Returns a ''User'' by the given ''username''
    *
    * @param username username of the requested user
    * @return ''User'' or ''null'' if a user with the given ''username'' not found
    */
  def findByUsername(username: String): User

  def findByTrackerToken(token: String): User

}
