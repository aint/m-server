package ua.softgroup.matrix.server.desktop.api;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportsContainerDataModel;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.UserService;

import java.util.NoSuchElementException;

import static ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus.SUCCESS;

@Service
public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private static final String CWD = System.getProperty("user.dir");
    private static final String FILE_EXTENSION = "png";

    private final UserService userService;
    private final ReportService reportService;
    private final ProjectService projectService;
    private final ClientSettingsService clientSettingsService;
    private final TrackingService trackingService;

    @Autowired
    public MatrixServerApiImpl(UserService userService,
                               ReportService reportService,
                               ProjectService projectService,
                               ClientSettingsService clientSettingsService,
                               TrackingService trackingService) {
        this.userService = userService;
        this.reportService = reportService;
        this.projectService = projectService;
        this.clientSettingsService = clientSettingsService;
        this.trackingService = trackingService;
    }


    @Override
    public ResponseModel<InitializeModel> authenticate(AuthModel authModel) {
        LOG.info("Authenticate {}, {}", authModel);
        String token = userService.authenticate(authModel);
        if (token == null) {
            return new ResponseModel<>(ResponseStatus.INVALID_CREDENTIALS);
        }
        ResponseModel<InitializeModel> responseModel = new ResponseModel<>(SUCCESS);
        ClientSettings clientSettings = clientSettingsService.getAll().stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        InitializeModel initializeModel = new InitializeModel(
                token,
                projectService.getUserActiveProjects(token),
                clientSettings.getStartDowntimeAfterInMinutes(), clientSettings
                .getScreenshotUpdateFrequentlyInMinutes(),
                10);
        responseModel.setContainer(Optional.of(initializeModel));
        return responseModel;
    }

    @Override
    public ResponseModel<ReportsContainerDataModel> getProjectReports(RequestModel requestModel) {
        Long projectId = requestModel.getProjectId();
        String token = requestModel.getToken();

        return new ResponseModel<>(new ReportsContainerDataModel(reportService.getReportsOf(token, projectId)));
    }

    @Override
    public ResponseModel saveReport(RequestModel<ReportModel> reportRequestModel) {
        ReportModel reportModel = reportRequestModel.getDataContainer().get();
        String token = reportRequestModel.getToken();

        return new ResponseModel<>(reportService.saveOrUpdate(token, reportModel));
    }

    @Override
    public ResponseModel startWork(RequestModel requestModel) {
        projectService.saveStartWorkTime(requestModel.getToken(), requestModel.getProjectId());

        return new ResponseModel<>(SUCCESS);
    }

    @Override
    public ResponseModel endWork(RequestModel requestModel) {
        projectService.saveEndWorkTime(requestModel.getToken(), requestModel.getProjectId());

        return new ResponseModel<>(SUCCESS);
    }

//    @Override
//    public Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId) {
//        LOG.debug("Requested project id {}", projectId);
//        return reportService.getAllReportsOf(tokenModel.getToken(), projectId);
//    }
//
//    @Override
//    public Set<ProjectModel> getUserActiveProjects(TokenModel tokenModel) {
//        return projectService.getUserActiveProjects(tokenModel.getToken());
//    }
//
//    @Override
//    public void startDowntime(TimeModel downtimeModel) {
//        LOG.debug("startDowntime DownTimeModel {}", downtimeModel);
//        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
//        project.setIdleStarted(LocalDateTime.now());
//        projectService.save(project);
//    }
//
//    @Override
//    public void endDowntime(TimeModel downtimeModel) {
//        LOG.debug("endDowntime DownTimeModel {}", downtimeModel);
//        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
//        LOG.debug("endDowntime Project {}", project);
//        LocalDateTime startTime = project.getIdleStarted();
//        if (startTime != null) {
//            Duration duration = Duration.between(startTime, LocalDateTime.now());
//            LOG.debug("Downtime in minutes {}", duration.toMinutes());
//            LOG.debug("Downtime in millis {}", duration.toMillis());
//            project.setIdleMinutes(project.getIdleMinutes() + duration.toMinutes());
//            project.setIdleStarted(null);
//            projectService.save(project);
//
//            WorkDay workDay = workDayService.getByDateAndProject(LocalDate.now(), project).orElse(new WorkDay(0L, 0L, project));
//            workDay.setIdleMinutes(workDay.getIdleMinutes() + duration.toMinutes());
//            workDayService.save(workDay);
//        }
//
//    }
//
//    @Override
//    public ClientSettingsModel getClientSettings() {
//        return clientSettingsService.getAll().stream()
//                .findFirst()
//                .map(this::convertClientSettingsToModel)
//                .orElseThrow(NoSuchElementException::new);
//    }
//
//    @Override
//    public TimeModel getTodayWorkTime(TimeModel timeModel) {
//        LOG.debug("getTodayWorkTime: {}", timeModel);
//        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
//        LocalDateTime startedWork = project.getWorkStarted();
//        Long todayMinutes = project.getTodayMinutes();
//        if (startedWork != null) {
//            Duration duration = Duration.between(startedWork, LocalDateTime.now());
//            LOG.debug("getTodayWorkTime: Current work time in minutes {}", todayMinutes + duration.toMinutes());
//        }
//        Long hours = todayMinutes / 60;
//        Long minutes = todayMinutes - hours * 60;
//        LOG.debug("getTodayWorkTime: hours {}, minutes {}", hours, minutes);
//        return new TimeModel(hours, minutes);
//    }
//
//    @Override
//    public TimeModel getTotalWorkTime(TimeModel timeModel) {
//        LOG.debug("getTotalWorkTime: {}", timeModel);
//        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
//        Long totalMinutes = project.getTotalMinutes();
//        Long hours = totalMinutes / 60;
//        Long minutes = totalMinutes - hours * 60;
//        Long downtime = project.getIdleMinutes();
//        double downtimePercent = Math.floor(downtime * 100 / Double.valueOf(totalMinutes) * 100) / 100;
//        LOG.debug("getTotalWorkTime: hours {}, minutes {}, downtime {}%", hours, minutes, downtimePercent);
//        return new TimeModel(hours, minutes, downtimePercent);
//    }
//
//    @Override
//    public void saveKeyboardLog(WriteKeyboard writeKeyboard) {
//        LOG.debug("saveKeyboardLog: {}", writeKeyboard);
//        Project project = projectService.getById(writeKeyboard.getProjectID()).orElseThrow(NoSuchElementException::new);
//        LOG.info("saveKeyboardLog: {}", project);
//        Tracking tracking = trackingService.getByProjectIdAndDate(writeKeyboard.getProjectID(), LocalDate.now());
//        tracking.setKeyboardText(tracking.getKeyboardText() + writeKeyboard.getWords());
//        trackingService.save(tracking);
//    }
//
//    @Override
//    public void saveActiveWindowsLog(ActiveWindowsModel activeWindows) {
//        LOG.debug("saveActiveWindowsLog: {}", activeWindows);
//        Project project = projectService.getById(activeWindows.getProjectId()).orElseThrow(NoSuchElementException::new);
//        LOG.info("saveActiveWindowsLog: {}", project);
//        Tracking tracking = trackingService.getByProjectIdAndDate(activeWindows.getProjectId(), LocalDate.now());
//        tracking.setWindowTimeMap(activeWindows.getWindowTimeMap());
//        trackingService.save(tracking);
//    }
//
//    @Transactional
//    @Override
//    public void saveScreenshot(ScreenshotModel file) {
//        LOG.debug("saveScreenshot: {}", file);
//        Project project = projectService.getById(file.getProjectID()).orElseThrow(NoSuchElementException::new);
//        LOG.info("saveScreenshot: {}", project);
//        try {
//            String filePath = CWD + environment.getProperty("screenshot.path") + System.currentTimeMillis() + "." + FILE_EXTENSION;
//            File screenshotFile = new File(filePath);
//            screenshotFile.getParentFile().mkdirs();
//            ImageIO.write(ImageIO.read(new ByteArrayInputStream(file.getFile())), FILE_EXTENSION, screenshotFile);
//            Tracking tracking = trackingService.getByProjectIdAndDate(file.getProjectID(), LocalDate.now());
//            tracking.getScreenshots().add(filePath);
//            trackingService.save(tracking);
//        } catch (Exception e) {
//            LOG.error("Failed to save screenshot", e);
//        }
//    }
//
//    private ClientSettingsModel convertClientSettingsToModel(ClientSettings settings) {
//        return new ClientSettingsModel(
//                0,
//                settings.getScreenshotUpdateFrequentlyInMinutes(),
//                settings.getKeyboardUpdateFrequentlyInMinutes(),
//                settings.getStartDowntimeAfterInMinutes());
//    }
}
