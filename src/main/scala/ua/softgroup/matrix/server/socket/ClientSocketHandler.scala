package ua.softgroup.matrix.server.socket

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import org.slf4j.{LoggerFactory, MDC}
import ua.softgroup.matrix.api.ServerCommands
import ua.softgroup.matrix.api.ServerCommands._
import ua.softgroup.matrix.api.model.datamodels._
import ua.softgroup.matrix.api.model.requestmodels.RequestModel
import ua.softgroup.matrix.api.model.responsemodels.{ResponseModel, ResponseStatus}
import ua.softgroup.matrix.server.socket.api.ServerSocketApi

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
class ClientSocketHandler(clientSocket: Socket, matrixServerApi: ServerSocketApi) extends Runnable {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private var objectOutputStream: ObjectOutputStream = _
  private var objectInputStream: ObjectInputStream = _

  override def run(): Unit = {
    MDC.put("IP", s"${clientSocket.getInetAddress.getHostAddress}")
    logger.info(s"Desktop client connected ${clientSocket.getInetAddress.getHostAddress}:${clientSocket.getPort}")

    try {
      objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream)
      objectInputStream = new ObjectInputStream(clientSocket.getInputStream)

      var serverCommand: ServerCommands = null
      while ({
        serverCommand = readServerCommand
        !clientRequestClose(serverCommand)
      }) processClientInput(serverCommand)

    } catch {
      case e: Exception =>
        logger.error("Error", e)
        sendFailAndClose()
    }
    MDC.clear()
  }

  private def readServerCommand = objectInputStream.readObject.asInstanceOf[ServerCommands]

  private def clientRequestClose(command: ServerCommands): Boolean = {
    if (CLOSE == command) {
      clientSocket.close()
      logger.info("Desktop client closed connection")
      return true
    }
    false
  }

  private def sendFailAndClose() = {
    logger.warn("Send FAIL and close")
    try {
      sendObject(new ResponseModel[DataModel](ResponseStatus.FAIL))
      clientSocket.close()
    } catch {
      case e: Exception => logger.error("Error while closing client connection", e)
    }
  }

  private def processClientInput(command: ServerCommands) = {
    logger.info("Desktop client sent command {}", command.name)

    command match {
      case AUTHENTICATE =>
        val authRequest = readObject.asInstanceOf[RequestModel[AuthModel]]
        sendObject(matrixServerApi.authenticate(authRequest))

      case GET_REPORTS =>
        val request = readObject.asInstanceOf[RequestModel[_ <: DataModel]]
        sendObject(matrixServerApi.getProjectReports(request))

      case SAVE_REPORT =>
        val reportRequest = readObject.asInstanceOf[RequestModel[ReportModel]]
        sendObject(matrixServerApi.saveReport(reportRequest))

      case START_WORK =>
        val requestModel = readObject.asInstanceOf[RequestModel[_ <: DataModel]]
        sendObject(matrixServerApi.startWork(requestModel))

      case END_WORK =>
        val requestModel = readObject.asInstanceOf[RequestModel[_ <: DataModel]]
        sendObject(matrixServerApi.endWork(requestModel))

      case CHECK_POINT =>
        val requestModel = readObject.asInstanceOf[RequestModel[CheckPointModel]]
        sendObject(matrixServerApi.processCheckpoint(requestModel))

      case SYNCHRONIZE =>
        val requestModel = readObject.asInstanceOf[RequestModel[SynchronizationModel]]
        sendObject(matrixServerApi.syncCheckpoints(requestModel))

      case CLOSE =>
        clientSocket.close()

      case _ =>
        logger.warn("No such command {}", command)
    }
  }

  private def sendObject(obj: Any) = {
    objectOutputStream.writeObject(obj)
    objectOutputStream.flush()
  }

  private def readObject = objectInputStream.readObject

}
