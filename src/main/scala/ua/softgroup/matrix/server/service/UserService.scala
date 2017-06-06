package ua.softgroup.matrix.server.service

import java.util.Optional

import ua.softgroup.matrix.api.model.datamodels.AuthModel
import ua.softgroup.matrix.server.persistent.entity.User

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait UserService extends GenericEntityService[User] {

  def authenticate(authModel: AuthModel): String

  def getByUsername(username: String): Optional[User]

  def getByTrackerToken(token: String): Optional[User]

}
