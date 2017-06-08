package ua.softgroup.matrix.server.service.impl

import java.io._
import java.time.{LocalDate, LocalDateTime, YearMonth}
import java.time.format.DateTimeFormatter
import java.util.Optional
import javax.imageio.ImageIO

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ua.softgroup.matrix.api.model.datamodels.ActiveWindowModel
import ua.softgroup.matrix.server.persistent.entity.{Screenshot, TrackingData, WindowTime}
import ua.softgroup.matrix.server.persistent.repository.TrackingDataRepository
import ua.softgroup.matrix.server.service._

import scala.util.{Failure, Try}
import scala.util.control.NonFatal

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
  */
@Service
class TrackingDataServiceImpl @Autowired() (
                              repository: TrackingDataRepository,
                              projectService: ProjectService,
                              userService: UserService,
                              workDayService: WorkDayService,
                              workTimePeriodService: WorkTimePeriodService) extends TrackingDataService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val SCREENSHOTS_FOLDER_PATH = System.getProperty("user.home") + "/screenshots"
  private val SCREENSHOT_EXT = "png"

  private val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

  @Transactional
  override def saveTrackingData(userToken: String, projectId: Long, keyboardText: String, mouseFootage: Double,
                                activeWindowList: List[ActiveWindowModel], screenshot: Array[Byte],
                                screenshotTitle: String): Unit = {

    logger.info(s"Saving tracking data: symbols ${keyboardText.length}, mouse $mouseFootage, windows ${activeWindowList.size}")

    val user = userService.getByTrackerToken(userToken).orElseThrow(() => new NoSuchElementException)
    val project = projectService.getById(projectId).orElseThrow(() => new NoSuchElementException)
    val workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now).orElseThrow(() => new NoSuchElementException)
    val workTimePeriod = workTimePeriodService.getLatestPeriodOf(workDay).orElseThrow(() => new NoSuchElementException)
    val trackingData = Optional.ofNullable(workTimePeriod.getTrackingData).orElseGet(() => new TrackingData(workTimePeriod))

    trackingData.setKeyboardText(trackingData.getKeyboardText + keyboardText)
    trackingData.setMouseFootage(trackingData.getMouseFootage + mouseFootage)
    if (screenshot != null) {

      autoCloseResource(new ByteArrayInputStream(screenshot)) {
        is => {
          val filePath = s"$SCREENSHOTS_FOLDER_PATH/${monthYearFormatter.format(YearMonth.now)}/${System.nanoTime}.$SCREENSHOT_EXT"
          val screenshotFile = new File(filePath)
          screenshotFile.getParentFile.mkdirs
          ImageIO.write(ImageIO.read(is), SCREENSHOT_EXT, screenshotFile)

          trackingData.getScreenshots.add(new Screenshot(LocalDateTime.now, screenshotTitle, screenshotFile.getPath, trackingData))
        }
      } recover {
        case e: Exception => logger.error("Failed to save screenshot", e)
      }
    }

    activeWindowList
      .foreach(entry => {
        val windowTime = new WindowTime(entry.getWindowTitle, entry.getStartTime, entry.getWorkingPeriodSeconds, trackingData)
        trackingData.getActiveWindows.add(windowTime)
      })
    workDay.setWindowsSwitchedCount(workDay.getWindowsSwitchedCount + activeWindowList.size)
    workDay.setSymbolsCount(workDay.getSymbolsCount + keyboardText.length)

    save(trackingData)
  }

  override def getById(id: Long): Optional[TrackingData] = Optional.ofNullable(repository.findOne(id))

  override def save(entity: TrackingData): TrackingData = repository.save(entity)

  override def isExist(id: Long): Boolean = repository.exists(id)

  //TODO extract this method to separate class
  private def autoCloseResource[C <: Closeable, R](resource: => C)(fun: C => R): Try[R] =
    Try(resource).flatMap(resourceInstance => {
      try {
        val returnValue = fun(resourceInstance)
        Try(resourceInstance.close()).map(_ => returnValue)
      } catch {
        case NonFatal(exceptionInFunction) =>
          try {
            resourceInstance.close()
            Failure(exceptionInFunction)
          } catch {
            case NonFatal(exceptionInClose) =>
              exceptionInFunction.addSuppressed(exceptionInClose)
              Failure(exceptionInFunction)
          }
      }
    })

}