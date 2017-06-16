package ua.softgroup.matrix.server.supervisor.producer.exception

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
class JwtException(val cause: Throwable) extends RuntimeException(cause)
