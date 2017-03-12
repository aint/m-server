package ua.softgroup.matrix.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Tracking;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.repository.TrackingRepository;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@PropertySource("classpath:desktop.properties")
public class TrackingServiceImpl extends AbstractEntityTransactionalService<Tracking> implements TrackingService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingServiceImpl.class);

    private static final String CWD = System.getProperty("user.dir");
    private static final String FILE_EXTENSION = "png";

    private final ProjectService projectService;
    private final UserService userService;
    private final WorkDayService workDayService;
    private final Environment environment;

    @Autowired
    public TrackingServiceImpl(TrackingRepository repository, ProjectService projectService,
                               UserService userService, WorkDayService workDayService,
                               Environment environment) {
        super(repository);
        this.projectService = projectService;
        this.userService = userService;
        this.workDayService = workDayService;
        this.environment = environment;
    }

    @Nonnull
    @Override
    public Tracking getByProjectIdAndDate(String userToken, Long projectId, LocalDate date) {
        User user = userService.getByTrackerToken(userToken).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        WorkDay workDay = workDayService.getByAuthorAndProjectAndDate(user, project, date).orElseThrow(NoSuchElementException::new);
        Tracking tracking = getRepository().findByWorkDay(workDay);
        return tracking != null ? tracking : new Tracking(workDay);
    }

    @Override
    @Transactional
    public void saveTrackingData(String userToken, Long projectId, String keyboardText, Double mouseFootage, Map<String, Integer> windowsTimeMap, byte[] screenshot) {
        Tracking trackingData = getByProjectIdAndDate(userToken, projectId, LocalDate.now());
        logger.debug("KeyboardLog {}", keyboardText);
        trackingData.setKeyboardText(trackingData.getKeyboardText() + keyboardText);

        logger.debug("MouseFootage {}", mouseFootage);
        trackingData.setMouseFootage(trackingData.getMouseFootage() + mouseFootage);

        logger.debug("ActiveWindows {}", windowsTimeMap);
        //TODO use entity graph to fetch map in one query
        trackingData.getWindowTimeMap().forEach((k, v) -> windowsTimeMap.merge(k, v, Integer::sum));
        trackingData.setWindowTimeMap(windowsTimeMap);

        try (InputStream is = new ByteArrayInputStream(screenshot)) {
            String filePath = CWD + environment.getProperty("screenshot.path") + System.currentTimeMillis() + "." + FILE_EXTENSION;
            File screenshotFile = new File(filePath);
            screenshotFile.getParentFile().mkdirs();
            ImageIO.write(ImageIO.read(is), FILE_EXTENSION, screenshotFile);
//            trackingData.getScreenshots().add(filePath);
        } catch (Exception e) {
            logger.error("Failed to save screenshot", e);
        }

        save(trackingData);
    }

    @Override
    protected TrackingRepository getRepository() {
        return (TrackingRepository) repository;
    }
}
