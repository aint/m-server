package ua.softgroup.matrix.server.service

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait TrackerSettingsService {

  def getTrackerSettings(token: String): TrackerSettings

  case class TrackerSettings(var checkpointFrequencyInSeconds: Int,
                        var screenshotPeriodFrequency: Int,
                        var startIdleAfterSeconds: Int)

}
