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
import ua.softgroup.matrix.server.persistent.repository.TrackingDataRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingDataService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TrackingDataServiceImpl extends AbstractEntityTransactionalService<TrackingData> implements TrackingDataService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingDataServiceImpl.class);

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;

    @Autowired
    public TrackingDataServiceImpl(TrackingDataRepository repository, ProjectService projectService,
                                   UserService userService, WorkDayService workDayService) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
    }

    @Override
    @Transactional
    public void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage,
                                 List<ActiveWindowModel> activeWindowList, byte[] screenshot) {

        logger.info("Saving tracking data: symbols {}, mouse {}, windows {}", keyboardText.length(), mouseFootage, activeWindowList.size());

        TrackingData trackingData = getTrackingDataOf(userToken, projectId, LocalDate.now());
        trackingData.setKeyboardText(trackingData.getKeyboardText() + keyboardText);
        trackingData.setMouseFootage(trackingData.getMouseFootage() + mouseFootage);
        if (screenshot != null) {
            trackingData.getScreenshots().add(new Screenshot(screenshot, LocalDateTime.now(), trackingData));
        }
        activeWindowList.stream()
                .map(entry -> new WindowTime(entry.getWindowTitle(), entry.getStartTime(), entry.getWorkingPeriodSeconds(), trackingData))
                .forEach(windowTime -> trackingData.getActiveWindows().add(windowTime));

        save(trackingData);
    }

    private TrackingData getTrackingDataOf(String userToken, Long projectId, LocalDate date) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, date).orElseThrow(NoSuchElementException::new);
        TrackingData trackingData = getRepository().findByWorkDay(workDay);
        return trackingData != null ? trackingData : new TrackingData(workDay);
    }

    @Override
    protected TrackingDataRepository getRepository() {
        return (TrackingDataRepository) repository;
    }
}
