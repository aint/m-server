package ua.softgroup.matrix.server.supervisor.producer.config

import javax.validation.{ConstraintViolationException, ValidationException}
import javax.ws.rs.core.Response
import javax.ws.rs.ext.{ExceptionMapper, Provider}

import org.hibernate.validator.internal.engine.path.PathImpl
import ua.softgroup.matrix.server.supervisor.producer.json.v2.ErrorJson
import Response.Status._

import scala.collection.JavaConverters._

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
@Provider
class ValidationExceptionMapper extends ExceptionMapper[ValidationException]{

  override def toResponse(e: ValidationException): Response = {
    val message = asScalaSet(e.asInstanceOf[ConstraintViolationException].getConstraintViolations)
      .map(cv => cv.getPropertyPath.asInstanceOf[PathImpl].getLeafNode.getName + " " + cv.getMessage)
      .mkString("; ")

    Response
      .status(BAD_REQUEST)
      .entity(new ErrorJson(message))
      .build
  }

}
