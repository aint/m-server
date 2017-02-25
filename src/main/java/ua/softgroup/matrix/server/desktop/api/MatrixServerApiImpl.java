package ua.softgroup.matrix.server.desktop.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;
import ua.softgroup.matrix.server.desktop.model.ClientSettingsModel;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.desktop.model.ScreenshotModel;
import ua.softgroup.matrix.server.desktop.model.TimeModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Tracking;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTimePeriod;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;
import ua.softgroup.matrix.server.service.WorkTimePeriodService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@PropertySource("classpath:desktop.properties")
public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private static final String CWD = System.getProperty("user.dir");
    private static final String FILE_EXTENSION = "png";

    private final UserService userService;
    private final ReportService reportService;
    private final ProjectService projectService;
    private final ClientSettingsService clientSettingsService;
    private final WorkTimePeriodService workTimePeriodService;
    private final TrackingService trackingService;
    private final WorkDayService workDayService;
    private final Environment environment;

    @Autowired
    public MatrixServerApiImpl(UserService userService,
                               ReportService reportService,
                               ProjectService projectService,
                               ClientSettingsService clientSettingsService,
                               WorkTimePeriodService workTimePeriodService,
                               TrackingService trackingService,
                               WorkDayService workDayService,
                               Environment environment) {
        this.userService = userService;
        this.reportService = reportService;
        this.projectService = projectService;
        this.clientSettingsService = clientSettingsService;
        this.workTimePeriodService = workTimePeriodService;
        this.trackingService = trackingService;
        this.workDayService = workDayService;
        this.environment = environment;
    }


    @Override
    public String authenticate(UserPassword userPassword) {
        LOG.info("Authenticate {}, {}", userPassword);
        return userService.authenticate(userPassword);
    }

    @Override
    public Constants saveReport(ReportModel reportModel) {
        LOG.debug("saveReport: {}", reportModel);

        if (reportModel.getId() == 0
                && reportService.ifReportExistForToday(reportModel.getToken(), reportModel.getProjectId())) {
            LOG.warn("Report exists and can't be updated without id");
            return Constants.REPORT_EXISTS;
        }

        long reportEditablePeriod = Long.parseLong(environment.getProperty("report.editable.days")) * 24;
        return reportService.saveOrUpdate(reportModel, reportEditablePeriod);
    }

    @Override
    public Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId) {
        LOG.debug("Requested project id {}", projectId);
        return reportService.getAllReportsOf(tokenModel.getToken(), projectId);
    }

    @Override
    public Set<ProjectModel> getUserActiveProjects(TokenModel tokenModel) {
        return projectService.getUserActiveProjects(tokenModel.getToken());
    }

    @Override
    public void startWork(TimeModel timeModel) {
        LOG.debug("Start work. TimeModel {} ", timeModel);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        project.setWorkStarted(LocalDateTime.now());
        projectService.save(project);

        workDayService.save(workDayService.getByDateAndProject(LocalDate.now(), project)
                                          .orElse(new WorkDay(0L, 0L, project)));
    }

    @Override
    public void endWork(TimeModel timeModel) {
        LOG.debug("TimeModel {}", timeModel);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        LocalDateTime startedWork = project.getWorkStarted();
        if (startedWork != null) {
            Duration duration = Duration.between(startedWork, LocalDateTime.now());
            long minutes = duration.toMinutes();
            LOG.debug("Work period in minutes {}", minutes);
            LOG.debug("Work period in millis {}", duration.toMillis());
            project.setWorkStarted(null);
            project.setTotalMinutes(project.getTotalMinutes() + minutes);
            project.setTodayMinutes(project.getTodayMinutes() + minutes);
            projectService.save(project);

            WorkDay workDay = workDayService.getByDateAndProject(LocalDate.now(), project).orElse(new WorkDay(0L, 0L, project));
            workDay.setWorkMinutes(workDay.getWorkMinutes() + minutes);
            workDayService.save(workDay);

            workTimePeriodService.save(new WorkTimePeriod(startedWork, LocalDateTime.now(), workDay));
        }
    }

    @Override
    public void startDowntime(TimeModel downtimeModel) {
        LOG.debug("startDowntime DownTimeModel {}", downtimeModel);
        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        project.setIdleStarted(LocalDateTime.now());
        projectService.save(project);
    }

    @Override
    public void endDowntime(TimeModel downtimeModel) {
        LOG.debug("endDowntime DownTimeModel {}", downtimeModel);
        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        LOG.debug("endDowntime Project {}", project);
        LocalDateTime startTime = project.getIdleStarted();
        if (startTime != null) {
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            LOG.debug("Downtime in minutes {}", duration.toMinutes());
            LOG.debug("Downtime in millis {}", duration.toMillis());
            project.setIdleMinutes(project.getIdleMinutes() + duration.toMinutes());
            project.setIdleStarted(null);
            projectService.save(project);

            WorkDay workDay = workDayService.getByDateAndProject(LocalDate.now(), project).orElse(new WorkDay(0L, 0L, project));
            workDay.setIdleMinutes(workDay.getIdleMinutes() + duration.toMinutes());
            workDayService.save(workDay);
        }

    }

    @Override
    public ClientSettingsModel getClientSettings() {
        return clientSettingsService.getAll().stream()
                .findFirst()
                .map(this::convertClientSettingsToModel)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public TimeModel getTodayWorkTime(TimeModel timeModel) {
        LOG.debug("getTodayWorkTime: {}", timeModel);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        LocalDateTime startedWork = project.getWorkStarted();
        Long todayMinutes = project.getTodayMinutes();
        if (startedWork != null) {
            Duration duration = Duration.between(startedWork, LocalDateTime.now());
            LOG.debug("getTodayWorkTime: Current work time in minutes {}", todayMinutes + duration.toMinutes());
        }
        Long hours = todayMinutes / 60;
        Long minutes = todayMinutes - hours * 60;
        LOG.debug("getTodayWorkTime: hours {}, minutes {}", hours, minutes);
        return new TimeModel(hours, minutes);
    }

    @Override
    public TimeModel getTotalWorkTime(TimeModel timeModel) {
        LOG.debug("getTotalWorkTime: {}", timeModel);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        Long totalMinutes = project.getTotalMinutes();
        Long hours = totalMinutes / 60;
        Long minutes = totalMinutes - hours * 60;
        Long downtime = project.getIdleMinutes();
        double downtimePercent = Math.floor(downtime * 100 / Double.valueOf(totalMinutes) * 100) / 100;
        LOG.debug("getTotalWorkTime: hours {}, minutes {}, downtime {}%", hours, minutes, downtimePercent);
        return new TimeModel(hours, minutes, downtimePercent);
    }

    @Override
    public void saveKeyboardLog(WriteKeyboard writeKeyboard) {
        LOG.debug("saveKeyboardLog: {}", writeKeyboard);
        Project project = projectService.getById(writeKeyboard.getProjectID()).orElseThrow(NoSuchElementException::new);
        LOG.info("saveKeyboardLog: {}", project);
        Tracking tracking = trackingService.getByProjectIdAndDate(writeKeyboard.getProjectID(), LocalDate.now());
        tracking.setKeyboardText(tracking.getKeyboardText() + writeKeyboard.getWords());
        trackingService.save(tracking);
    }

    @Override
    public void saveActiveWindowsLog(ActiveWindowsModel activeWindows) {
        LOG.debug("saveActiveWindowsLog: {}", activeWindows);
        Project project = projectService.getById(activeWindows.getProjectId()).orElseThrow(NoSuchElementException::new);
        LOG.info("saveActiveWindowsLog: {}", project);
        Tracking tracking = trackingService.getByProjectIdAndDate(activeWindows.getProjectId(), LocalDate.now());
        tracking.setWindowTimeMap(activeWindows.getWindowTimeMap());
        trackingService.save(tracking);
    }

    @Transactional
    @Override
    public void saveScreenshot(ScreenshotModel file) {
        LOG.debug("saveScreenshot: {}", file);
        Project project = projectService.getById(file.getProjectID()).orElseThrow(NoSuchElementException::new);
        LOG.info("saveScreenshot: {}", project);
        try {
            String filePath = CWD + environment.getProperty("screenshot.path") + System.currentTimeMillis() + "." + FILE_EXTENSION;
            File screenshotFile = new File(filePath);
            screenshotFile.getParentFile().mkdirs();
            ImageIO.write(ImageIO.read(new ByteArrayInputStream(file.getFile())), FILE_EXTENSION, screenshotFile);
            Tracking tracking = trackingService.getByProjectIdAndDate(file.getProjectID(), LocalDate.now());
            tracking.getScreenshots().add(filePath);
            trackingService.save(tracking);
        } catch (Exception e) {
            LOG.error("Failed to save screenshot", e);
        }
    }

    private ClientSettingsModel convertClientSettingsToModel(ClientSettings settings) {
        return new ClientSettingsModel(
                0,
                settings.getScreenshotUpdateFrequentlyInMinutes(),
                settings.getKeyboardUpdateFrequentlyInMinutes(),
                settings.getStartDowntimeAfterInMinutes());
    }
}
