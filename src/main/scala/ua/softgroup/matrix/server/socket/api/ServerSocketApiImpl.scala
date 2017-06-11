package ua.softgroup.matrix.server.socket.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.api.model.datamodels._
import ua.softgroup.matrix.api.model.requestmodels.RequestModel
import ua.softgroup.matrix.api.model.responsemodels.{ResponseModel, ResponseStatus}
import ua.softgroup.matrix.server.service.{ProjectService, TrackerSettingsService, TrackingDataService, _}

import scala.collection.JavaConverters._

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Service
class ServerSocketApiImpl @Autowired() (userService: UserService,
                                        projectService: ProjectService,
                                        workDayService: WorkDayService,
                                        trackerSettingsService: TrackerSettingsService,
                                        trackingDataService: TrackingDataService) extends ServerSocketApi {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def authenticate(authRequestModel: RequestModel[AuthModel]): ResponseModel[InitializeModel] = {
    val authModel = authRequestModel.getDataContainer.or(() => dataContainerEmptyException)
    logger.info(s"Authentication of user '${authModel.getUsername}' with password '${authModel.getPassword}'")

    val token: String = userService.authenticate(authModel) match {
      case Some(t) => t
      case None => return new ResponseModel[InitializeModel](ResponseStatus.INVALID_CREDENTIALS)
    }
    val trackerSettings = trackerSettingsService.getTrackerSettings(token)

    new ResponseModel[InitializeModel](
      new InitializeModel(
        token,
        projectService.getUserActiveProjects(token),
        trackerSettings.startIdleAfterSeconds,
        trackerSettings.screenshotPeriodFrequency,
        trackerSettings.checkpointFrequencyInSeconds,
        null)
    )
  }

  override def getProjectReports(requestModel: RequestModel[_ <: DataModel]): ResponseModel[ReportsContainerDataModel] = {
    val projectId = requestModel.getProjectId
    val token = requestModel.getToken

    logger.info(s"User '$token' request reports of project '$projectId'")

    new ResponseModel[ReportsContainerDataModel](
      new ReportsContainerDataModel(workDayService.getWorkDaysOf(token, projectId))
    )
  }

  override def saveReport(reportRequestModel: RequestModel[ReportModel]): ResponseModel[_ <: DataModel] = {
    val reportModel = reportRequestModel.getDataContainer.or(() => dataContainerEmptyException)
    val token = reportRequestModel.getToken
    val projectId = reportRequestModel.getProjectId

    logger.info(s"User '$token' save report to project '$projectId'")

    new ResponseModel[DataModel](workDayService.saveReportOrUpdate(token, projectId, reportModel))
  }

  override def startWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel] = {
    val token = requestModel.getToken
    val projectId = requestModel.getProjectId

    logger.info(s"User '$token' start work on project '$projectId'")

    new ResponseModel[TimeModel](projectService.saveStartWorkTime(token, projectId))
  }

  override def endWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel] = {
    val token = requestModel.getToken
    val projectId = requestModel.getProjectId

    logger.info(s"User '$token' end work on project '$projectId'")

    new ResponseModel[TimeModel](projectService.saveEndWorkTime(token, projectId))
  }

  override def processCheckpoint(requestModel: RequestModel[CheckPointModel]): ResponseModel[TimeModel] = {
    val checkPointModel = requestModel.getDataContainer.or(() => dataContainerEmptyException)

    logger.info(s"Process checkpoint of user '${requestModel.getToken} on project '${requestModel.getProjectId}'")

    trackingDataService.saveTrackingData(
      requestModel.getToken,
      requestModel.getProjectId,
      checkPointModel.getKeyboardLogs,
      checkPointModel.getMouseFootage,
      asScalaBuffer(checkPointModel.getActiveWindows).toList,
      checkPointModel.getScreenshot,
      checkPointModel.getScreenshotWindowTitle)

    new ResponseModel[TimeModel](projectService.saveCheckpointTime(
      requestModel.getToken, requestModel.getProjectId, checkPointModel.getIdleTime)
    )
  }

  override def syncCheckpoints(requestModel: RequestModel[SynchronizationModel]): ResponseModel[_ <: DataModel] = {
    logger.info(s"Sync checkpoints of user '${requestModel.getToken} on project '${requestModel.getProjectId}'")

    requestModel.getDataContainer.or(() => dataContainerEmptyException)
      .getCheckPointModels.forEach((checkPointModel: CheckPointModel) => {
        trackingDataService.saveTrackingData(
          requestModel.getToken,
          requestModel.getProjectId,
          checkPointModel.getKeyboardLogs,
          checkPointModel.getMouseFootage,
          asScalaBuffer(checkPointModel.getActiveWindows).toList,
          checkPointModel.getScreenshot,
          checkPointModel.getScreenshotWindowTitle)

        projectService.saveCheckpointTime(
          requestModel.getToken,
          requestModel.getProjectId,
          checkPointModel.getIdleTime)
    })

    new ResponseModel[DataModel](ResponseStatus.SUCCESS)
  }

  private def dataContainerEmptyException = throw new RuntimeException("Data container is empty")

}
