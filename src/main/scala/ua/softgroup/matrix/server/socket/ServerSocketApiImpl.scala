package ua.softgroup.matrix.server.socket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.softgroup.matrix.api.model.datamodels._
import ua.softgroup.matrix.api.model.requestmodels.RequestModel
import ua.softgroup.matrix.api.model.responsemodels.{ResponseModel, ResponseStatus}
import ua.softgroup.matrix.server.service.{ProjectService, TrackerSettingsService, TrackingDataService, _}

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Service
class ServerSocketApiImpl @Autowired() (userService: UserService,
                                        projectService: ProjectService,
                                        workDayService: WorkDayService,
                                        trackerSettingsService: TrackerSettingsService,
                                        trackingDataService: TrackingDataService) extends ServerSocketApi {


  override def authenticate(authRequestModel: RequestModel[AuthModel]): ResponseModel[InitializeModel] = {
    val authModel = authRequestModel.getDataContainer.or(() => dataContainerEmptyException)
    val token = userService.authenticate(authModel)
    if (token == null) return new ResponseModel[InitializeModel](ResponseStatus.INVALID_CREDENTIALS)
    val trackerSettings = trackerSettingsService.getTrackerSettings(token)

    new ResponseModel[InitializeModel](
      new InitializeModel(
        token,
        projectService.getUserActiveProjects(token),
        trackerSettings.getStartIdleAfterSeconds,
        trackerSettings.getScreenshotPeriodFrequency,
        trackerSettings.getCheckpointFrequencyInSeconds,
        null)
    )
  }

  override def getProjectReports(requestModel: RequestModel[_ <: DataModel]): ResponseModel[ReportsContainerDataModel] = {
    val projectId = requestModel.getProjectId
    val token = requestModel.getToken

    new ResponseModel[ReportsContainerDataModel](
      new ReportsContainerDataModel(workDayService.getWorkDaysOf(token, projectId))
    )
  }

  override def saveReport(reportRequestModel: RequestModel[ReportModel]): ResponseModel[_ <: DataModel] = {
    val reportModel = reportRequestModel.getDataContainer.or(() => dataContainerEmptyException)
    val token = reportRequestModel.getToken
    val projectId = reportRequestModel.getProjectId

    new ResponseModel[DataModel](workDayService.saveReportOrUpdate(token, projectId, reportModel))
  }

  override def startWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel] = {
    val token = requestModel.getToken
    val projectId = requestModel.getProjectId

    new ResponseModel[TimeModel](projectService.saveStartWorkTime(token, projectId))
  }

  override def endWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel] = {
    val token = requestModel.getToken
    val projectId = requestModel.getProjectId

    new ResponseModel[TimeModel](projectService.saveEndWorkTime(token, projectId))
  }

  override def processCheckpoint(requestModel: RequestModel[CheckPointModel]): ResponseModel[TimeModel] = {
    val checkPointModel = requestModel.getDataContainer.or(() => dataContainerEmptyException)

    trackingDataService.saveTrackingData(
      requestModel.getToken,
      requestModel.getProjectId,
      checkPointModel.getKeyboardLogs,
      checkPointModel.getMouseFootage,
      checkPointModel.getActiveWindows,
      checkPointModel.getScreenshot,
      checkPointModel.getScreenshotWindowTitle)

    new ResponseModel[TimeModel](projectService.saveCheckpointTime(
      requestModel.getToken, requestModel.getProjectId, checkPointModel.getIdleTime)
    )
  }

  override def syncCheckpoints(requestModel: RequestModel[SynchronizationModel]): ResponseModel[_ <: DataModel] = {
    requestModel.getDataContainer.or(() => dataContainerEmptyException)
      .getCheckPointModels.forEach((checkPointModel: CheckPointModel) => {
        trackingDataService.saveTrackingData(
          requestModel.getToken,
          requestModel.getProjectId,
          checkPointModel.getKeyboardLogs,
          checkPointModel.getMouseFootage,
          checkPointModel.getActiveWindows,
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
