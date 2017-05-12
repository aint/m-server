package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.api.model.datamodels.ActiveWindowModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Screenshot;
import ua.softgroup.matrix.server.persistent.entity.TrackingData;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WindowTime;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.persistent.repository.TrackingDataRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingDataService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.service.WorkTimePeriodService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrackingDataServiceImpl extends AbstractEntityTransactionalService<TrackingData> implements TrackingDataService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingDataServiceImpl.class);

    private static final String CWD = System.getProperty("user.dir");
    private static final String SCREENSHOT_EXT = "png";

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;
    private final WorkTimePeriodService workTimePeriodService;

    @Autowired
    public TrackingDataServiceImpl(TrackingDataRepository repository, ProjectService projectService,
                                   UserService userService, WorkDayService workDayService,
                                   WorkTimePeriodService workTimePeriodService) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
        this.workTimePeriodService = workTimePeriodService;
    }

    @Override
    @Transactional
    public void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage,
                                 List<ActiveWindowModel> activeWindowList, byte[] screenshot, String screenshotTitle) {

        logger.info("Saving tracking data: symbols {}, mouse {}, windows {}", keyboardText.length(), mouseFootage, activeWindowList.size());

        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, LocalDate.now()).orElseThrow(NoSuchElementException::new);
        WorkTimePeriod workTimePeriod = workTimePeriodService.getLatestPeriodOf(workDay).orElseThrow(NoSuchElementException::new);
        TrackingData trackingData = Optional.ofNullable(workTimePeriod.getTrackingData())
                                            .orElseGet(() -> new TrackingData(workTimePeriod));

//        TrackingData trackingData = getTrackingDataOf(userToken, projectId, LocalDate.now());
        trackingData.setKeyboardText(trackingData.getKeyboardText() + keyboardText);
        workDay.setSymbolsCount(workDay.getSymbolsCount() + keyboardText.length());
        trackingData.setMouseFootage(trackingData.getMouseFootage() + mouseFootage);
        if (screenshot != null) {

            try (InputStream is = new ByteArrayInputStream(screenshot)) {
                File screenshotFile = new File(CWD + "/screenshots/" + System.nanoTime() + "." + SCREENSHOT_EXT);
                screenshotFile.getParentFile().mkdirs();
                ImageIO.write(ImageIO.read(is), SCREENSHOT_EXT, screenshotFile);

                trackingData.getScreenshots().add(new Screenshot(LocalDateTime.now(), screenshotTitle, screenshotFile.getPath(), trackingData));
            } catch (Exception e) {
                logger.error("Failed to save screenshot", e);
            }
        }
        activeWindowList.stream()
                .map(entry -> new WindowTime(entry.getWindowTitle(), entry.getStartTime(), entry.getWorkingPeriodSeconds(), trackingData))
                .forEach(windowTime -> trackingData.getActiveWindows().add(windowTime));
        workDay.setWindowsSwitchedCount(workDay.getWindowsSwitchedCount() + activeWindowList.size());

        save(trackingData);
    }

//    private TrackingData getTrackingDataOf(String userToken, Long projectId, LocalDate date) {
//        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
//        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
//        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, date).orElseThrow(NoSuchElementException::new);
//        TrackingData trackingData = getRepository().findByWorkDay(workDay);
//        return trackingData != null ? trackingData : new TrackingData(workDay);
//    }

    @Override
    protected TrackingDataRepository getRepository() {
        return (TrackingDataRepository) repository;
    }
}
