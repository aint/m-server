package ua.softgroup.matrix.server.service.impl

import java.io.IOException
import java.util.Optional

import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Response
import ua.softgroup.matrix.api.model.datamodels.AuthModel
import ua.softgroup.matrix.server.persistent.entity.User
import ua.softgroup.matrix.server.persistent.repository.UserRepository
import ua.softgroup.matrix.server.service.UserService
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint
import ua.softgroup.matrix.server.supervisor.consumer.json.{LoginJson, UserJson}

import scala.util.{Failure, Success, Try}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Service
class UserServiceImpl @Autowired() (repository: UserRepository,
                                    supervisorEndpoint: SupervisorEndpoint) extends UserService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def authenticate(authModel: AuthModel): Option[String] = {
    val username = authModel.getUsername
    val password = authModel.getPassword

    Try(authenticate(username, password)) match {
      case Success(optionToken) => optionToken
      case Failure(f) =>
        logger.error("Authentication error", f.getCause)
        authenticateFromDb(username, password)
    }
  }

  private def authenticateFromDb(username: String, password: String): Option[String] = {
    val user = repository.findByUsername(username)
    if (user != null && BCrypt.checkpw(password, user.getPassword)) {
      logger.info(s"Offline authentication. User '$username' authenticated successfully with token '${user.getTrackerToken}'")
      Option(user.getTrackerToken)
    }
    logger.info(s"Offline authentication. User '$username' not found or passwords aren't equals")
    Option.empty
  }

  private def authenticate(username: String, password: String): Option[String] = {
    val loginJson = executeLoginQuery(username, password).body
    if (loginJson.getSuccess) {
      saveUser(loginJson.getUser, password)
      val token = loginJson.getTrackerToken
      logger.info(s"User '$username' authenticated successfully with token '$token'")
      return Option(token)
    }
    logger.info(s"Authentication failed: ${loginJson.getMessage}")
    Option.empty
  }

  private def executeLoginQuery(username: String, password: String): Response[LoginJson] = {
    val response = supervisorEndpoint
      .login(username, password)
      .execute
    if (!response.isSuccessful) {
      throw new IOException(s"Oops... Something goes wrong. ${response.errorBody.string}")
    }
    response
  }

  private def saveUser(userJson: UserJson, password: String) = {
    logger.debug(s"UserJson $userJson")
    val user = Optional.ofNullable(repository.findOne(userJson.getId)).orElse(new User)
    logger.debug(s"UserEntity $user")
    user.setId(userJson.getId)
    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt))
    user.setUsername(userJson.getUsername)
    user.setTrackerToken(userJson.getTrackerToken)
    user.setExternalHourlyRate(userJson.getProfile.getExternalHourlyRate)
    user.setExternalHourlyRateCurrencyId(userJson.getProfile.getExternalHourlyRateCurrencyId)
    user.setInternalHourlyRate(userJson.getProfile.getInternalHourlyRate)
    user.setInternalHourlyRateCurrencyId(userJson.getProfile.getInternalHourlyRateCurrencyId)

    repository.save(user)
  }

  override def getByUsername(username: String): Option[User] = Option(repository.findByUsername(username))

  override def getByTrackerToken(token: String): Optional[User] = Optional.ofNullable(repository.findByTrackerToken(token))

  override def getById(id: Long): Optional[User] = Optional.ofNullable(repository.findOne(id))

  override def save(entity: User): User = repository.save(entity)

  override def isExist(id: Long): Boolean = repository.exists(id)

}