package ua.softgroup.matrix.server.socket.api

import ua.softgroup.matrix.api.model.datamodels._
import ua.softgroup.matrix.api.model.requestmodels.RequestModel
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
trait ServerSocketApi {

  def authenticate(authModel: RequestModel[AuthModel]): ResponseModel[InitializeModel]

  def getProjectReports(requestModel: RequestModel[_ <: DataModel]): ResponseModel[ReportsContainerDataModel]

  def saveReport(reportModel: RequestModel[ReportModel]): ResponseModel[_ <: DataModel]

  def startWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel]

  def endWork(requestModel: RequestModel[_ <: DataModel]): ResponseModel[_ <: DataModel]

  def processCheckpoint(requestModel: RequestModel[CheckPointModel]): ResponseModel[TimeModel]

  def syncCheckpoints(requestModel: RequestModel[SynchronizationModel]): ResponseModel[_ <: DataModel]

}
