package ua.softgroup.matrix.server.service

import ua.softgroup.matrix.api.model.datamodels.ActiveWindowModel
import ua.softgroup.matrix.server.persistent.entity.TrackingData

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait TrackingDataService extends GenericEntityService[TrackingData] {

  def saveTrackingData(userToken: String, projectId: Long, keyboardText: String, mouseFootage: Double,
                       activeWindowList: List[ActiveWindowModel], screenshot: Array[Byte], screenshotTitle: String)

}
