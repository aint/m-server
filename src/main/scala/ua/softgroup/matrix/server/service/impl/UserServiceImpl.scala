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

import scala.util.{Failure, Try}

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Service
class UserServiceImpl @Autowired() (repository: UserRepository,
                                    supervisorEndpoint: SupervisorEndpoint) extends UserService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def authenticate(authModel: AuthModel): String = {
    try
      tryToAuthenticate(authModel.getUsername, authModel.getPassword)
    catch {
      case e: IOException =>
        logger.warn("Authentication error: {}", e)
        authenticateFromDb(authModel.getUsername, authModel.getPassword)
    }
  }

  private def authenticateFromDb(username: String, password: String): String = {
    val user = repository.findByUsername(username)
    if (user != null && BCrypt.checkpw(password, user.getPassword)) {
      logger.info(s"Offline authentication. Given token ${user.getTrackerToken}")
      user.getTrackerToken
    }
    null
  }

  private def tryToAuthenticate(username: String, password: String): String = {
    val loginJson = executeLoginQuery(username, password).body
    if (loginJson.getSuccess) {
      saveUser(loginJson.getUser, password)
      val token = loginJson.getTrackerToken
      logger.info(s"User authenticated successfully: $token")
      return token
    }
    logger.info(s"Authentication failed: ${loginJson.getMessage}")
    null
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

  override def getByUsername(username: String): Optional[User] = Optional.ofNullable(repository.findByUsername(username))

  override def getByTrackerToken(token: String): Optional[User] = Optional.ofNullable(repository.findByTrackerToken(token))

  override def getById(id: Long): Optional[User] = Optional.ofNullable(repository.findOne(id))

  override def save(entity: User): User = repository.save(entity)

  private def logTry[A](computation: => A): Try[A] = {
    Try(computation) recoverWith {
      case e: IOException =>
        logger.error("Failed to encode screenshot to Base64", e)
        Failure(e)
    }
  }

  /**
    * Checks entity existence by the given primary ''id''
    *
    * @param id entity's primary key
    * @return ''true'' if an entity with the given ''id'' exists; ''false'' otherwise
    */
  override def isExist(id: Long): Boolean = repository.exists(id)
}