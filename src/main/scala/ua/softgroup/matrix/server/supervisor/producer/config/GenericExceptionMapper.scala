package ua.softgroup.matrix.server.supervisor.producer.config

import javax.ws.rs.{NotAuthorizedException, NotFoundException, WebApplicationException}
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.ext.{ExceptionMapper, Provider}

import com.nimbusds.jose.JOSEException
import org.slf4j.LoggerFactory
import ua.softgroup.matrix.server.supervisor.producer.exception.JwtException
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ErrorJson

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Provider
class GenericExceptionMapper extends ExceptionMapper[Throwable] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def toResponse(throwable: Throwable): Response = {
    if (!(throwable.isInstanceOf[NotFoundException] || throwable.isInstanceOf[NotAuthorizedException]))
      logger.warn("Jersey module exception: ", throwable)

    Response
      .status(getStatusType(throwable).getStatusCode)
      .entity(new ErrorJson(throwable.getLocalizedMessage))
      .`type`(MediaType.APPLICATION_JSON)
      .build
  }

  private def getStatusType(ex: Throwable) = {
    ex match {
      case e: WebApplicationException => e.getResponse.getStatusInfo
      case _: JOSEException | _: JwtException => Response.Status.FORBIDDEN
      case _ => Response.Status.INTERNAL_SERVER_ERROR
    }
  }

}
