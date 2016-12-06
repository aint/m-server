package ua.softgroup.matrix.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.model.ClientSettingsModel;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.SynchronizedModel;
import ua.softgroup.matrix.server.model.TimeModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.model.UserPassword;
import ua.softgroup.matrix.server.model.WriteKeyboard;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.entity.Keyboard;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.Screenshot;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkDay;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.persistent.entity.WorktimePeriod;
import ua.softgroup.matrix.server.persistent.repository.WorkDayRepository;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.MetricsService;
import ua.softgroup.matrix.server.service.PeriodService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final WorkTimeService workTimeService ;
    private final PeriodService periodService;
    private final MetricsService metricsService;
    private final WorkDayRepository workDayRepository;
    private final Environment environment;

    @Autowired
    public MatrixServerApiImpl(UserService userService,
                               ReportService reportService,
                               ProjectService projectService,
                               ClientSettingsService clientSettingsService,
                               WorkTimeService workTimeService,
                               PeriodService periodService,
                               MetricsService metricsService,
                               WorkDayRepository workDayRepository,
                               Environment environment) {
        this.userService = userService;
        this.reportService = reportService;
        this.projectService = projectService;
        this.clientSettingsService = clientSettingsService;
        this.workTimeService = workTimeService;
        this.periodService = periodService;
        this.metricsService = metricsService;
        this.workDayRepository = workDayRepository;
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

        if (reportModel.getId() == 0) {
            // save new
            //TODO reportService.getTodayReportsOf()
            User user = userService.getByTrackerToken(reportModel.getToken()).orElseThrow(NoSuchElementException::new);
            Project project = projectService.getById(reportModel.getProjectId()).orElseThrow(NoSuchElementException::new);
            for (Report report : reportService.getAllReportsOf(user, project)) {
                LocalDateTime creationDate = report.getCreationDate();
                LOG.warn("saveReport: creation date {}", creationDate);
                if (creationDate.isAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0))
                        && creationDate.isBefore(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))) {
                    LOG.warn("saveReport: exists");
                    return Constants.REPORT_EXISTS;
                }
            }
        }

        if (reportService.getById(reportModel.getId()).isPresent()) {
            Report report = reportService.getById(reportModel.getId()).get();
            return updateReport(report, reportModel);
        }

        reportService.save(reportModel);

        return Constants.TOKEN_VALIDATED;
    }

    private Constants updateReport(Report report, ReportModel reportModel) {
        LOG.debug("updateReport");
        Duration duration = Duration.between(report.getCreationDate(), LocalDateTime.now());
        LOG.debug("Report created {} hours ago", duration.toHours());
        final long reportEditablePeriod = Long.parseLong(environment.getProperty("report.editable.days")) * 24;
        if (duration.toHours() > reportEditablePeriod) {
            LOG.debug("Report expired");
            return Constants.REPORT_EXPIRED;
        }
        reportService.save(reportModel);

        return Constants.TOKEN_VALIDATED;
    }

    @Deprecated
    @Override
    public ReportModel getReport(ReportModel reportModel) {
        LOG.debug("getReport: {}", reportModel);

        Report report = reportService.getById(reportModel.getId()).orElseThrow(NoSuchElementException::new);
        LOG.debug("getReport: {}", report);

        reportModel.setId(report.getId());
        reportModel.setTitle(report.getTitle());
        reportModel.setDescription(report.getDescription());
        reportModel.setStatus(0);
        reportModel.setChecked(report.getWorkDay().isChecked());
        return reportModel;
    }

    @Override
    public Set<ReportModel> getAllReports(TokenModel tokenModel) {
        User user = userService.getByTrackerToken(tokenModel.getToken()).orElseThrow(NoSuchElementException::new);
        return reportService.getAllReportsOf(user).stream()
                .map(report -> reportService.convertEntityToDto(report, tokenModel.getToken()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId) {
        LOG.debug("Requested project id {}", projectId);
        User user = userService.getByTrackerToken(tokenModel.getToken()).orElseThrow(NoSuchElementException::new);
        Project project = projectService.getById(projectId).orElseThrow(NoSuchElementException::new);
        return reportService.getAllReportsOf(user, project).stream()
                .map(report -> reportService.convertEntityToDto(report, tokenModel.getToken()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ProjectModel> getUserActiveProjects(TokenModel tokenModel) {
        return projectService.getUserActiveProjects(tokenModel.getToken());
    }

    @Override
    public void setCurrentProject(Long projectId) {

    }

    @Override
    public void startWork(TimeModel timeModel) {
        LOG.debug("TimeModel {} ", timeModel);
        User user = userService.getByTrackerToken(timeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("User {} start work", user);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(null, project, user));
        userWorkTime.setStartedWork(LocalDateTime.now());
        workTimeService.save(userWorkTime);
    }

    @Override
    public void endWork(TimeModel timeModel) {
        LOG.debug("TimeModel {}", timeModel);
        User user = userService.getByTrackerToken(timeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("User {} end work", user);
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NoSuchElementException::new);
        LocalDateTime startedWork = userWorkTime.getStartedWork();
        if (startedWork != null) {
            Duration duration = Duration.between(startedWork, LocalDateTime.now());
            long minutes = duration.toMinutes();
            LOG.debug("Work period in minutes {}", minutes);
            LOG.debug("Work period in millis {}", duration.toMillis());
            userWorkTime.setStartedWork(null);
            userWorkTime.setTotalMinutes(userWorkTime.getTotalMinutes() + minutes);
            userWorkTime.setTodayMinutes(userWorkTime.getTodayMinutes() + minutes);
            workTimeService.save(userWorkTime);

            WorkDay todayWorkDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(LocalDate.now(), userWorkTime))
                    .orElse(new WorkDay(0L, 0L, userWorkTime));
            todayWorkDay.setWorkMinutes(todayWorkDay.getWorkMinutes() + minutes);
            workDayRepository.save(todayWorkDay);

            periodService.save(new WorktimePeriod(startedWork, LocalDateTime.now(), timeModel.isForeignRate(), todayWorkDay));
        }
    }

    @Override
    public void startDowntime(TimeModel downtimeModel) {
        LOG.debug("startDowntime DownTimeModel {}", downtimeModel);
        User user = userService.getByTrackerToken(downtimeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("startDowntime User {}", user);
        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NoSuchElementException::new);
        userWorkTime.setStartDowntime(LocalDateTime.now());
        workTimeService.save(userWorkTime);
    }

    @Override
    public void endDowntime(TimeModel downtimeModel) {
        LOG.debug("endDowntime DownTimeModel {}", downtimeModel);
        User user = userService.getByTrackerToken(downtimeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("endDowntime username {}", user.getUsername());
        Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NoSuchElementException::new);
        LOG.debug("endDowntime WorkTime {}", userWorkTime);
        LocalDateTime startTime = userWorkTime.getStartDowntime();
        if (startTime != null) {
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            LOG.debug("Downtime in minutes {}", duration.toMinutes());
            LOG.debug("Downtime in millis {}", duration.toMillis());
            userWorkTime.setDowntimeMinutes(userWorkTime.getDowntimeMinutes() + duration.toMinutes());
            userWorkTime.setStartDowntime(null);
            workTimeService.save(userWorkTime);

            WorkDay todayWorkDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(LocalDate.now(), userWorkTime))
                    .orElse(new WorkDay(0L, 0L, userWorkTime));
            todayWorkDay.setIdleMinutes(todayWorkDay.getIdleMinutes() + duration.toMinutes());
            workDayRepository.save(todayWorkDay);

//            periodService.save(new DowntimePeriod(startTime, LocalDateTime.now(), todayWorkDay));
        }

    }

    @Override
    public boolean sync(SynchronizedModel synchronizedModel) {
        LOG.warn("Sync {}", synchronizedModel);

        Optional.ofNullable(synchronizedModel.getReportModel())
                .ifPresent(reportModels -> reportModels.stream()
                        .filter(Objects::nonNull)
                        .forEach(this::saveReport));

        Optional.ofNullable(synchronizedModel.getTimeModel())
                .ifPresent(timeModels -> timeModels.stream()
                        .filter(Objects::nonNull)
                        .forEach(timeModel -> {
                                LOG.warn("OFFLINE Timemodel {}", timeModel);
                                User user = userService.getByTrackerToken(timeModel.getToken()).orElseThrow(NoSuchElementException::new);
                                Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
                                WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(null, project, user));
                                LOG.warn("OFFLINE {}", userWorkTime);
                                LOG.debug("WorkTime username {}, projectId {} ", user.getUsername(), project.getId());
                                userWorkTime.setStartedWork(null);
                                userWorkTime.setTotalMinutes(userWorkTime.getTotalMinutes() + timeModel.getMinute());
                                userWorkTime.setTodayMinutes(userWorkTime.getTodayMinutes() + timeModel.getMinute());
                                workTimeService.save(userWorkTime);

                                WorkDay todayWorkDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(LocalDate.now(), userWorkTime))
                                        .orElse(new WorkDay(0L, 0L, userWorkTime));
                                todayWorkDay.setWorkMinutes(todayWorkDay.getWorkMinutes() + timeModel.getMinute());
                                workDayRepository.save(todayWorkDay);

                                periodService.save(new WorktimePeriod(LocalDateTime.now().minusMinutes(timeModel.getMinute()), LocalDateTime.now(), timeModel.isForeignRate(), todayWorkDay));
                        }));

        Optional.ofNullable(synchronizedModel.getDowntimeModel())
                .ifPresent(downtimeModels -> downtimeModels.stream()
                        .filter(Objects::nonNull)
                        .forEach(downtimeModel -> {
                                LOG.warn("Offline Downtime {}", downtimeModel);
                                User user = userService.getByTrackerToken(downtimeModel.getToken()).orElseThrow(NoSuchElementException::new);
                                Project project = projectService.getById(downtimeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
                                LOG.warn("User id {}, Project id {}", user.getId(), project.getId());
                                WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElseThrow(NoSuchElementException::new);
                                LOG.debug("WorkTime {}", userWorkTime);
                                userWorkTime.setStartDowntime(null);
                                long diff = TimeUnit.MILLISECONDS.toMinutes(downtimeModel.getHours() - downtimeModel.getMinute());
                                LOG.warn("Downtime diff {}", diff);
                                userWorkTime.setDowntimeMinutes(diff > 0 ? diff : userWorkTime.getDowntimeMinutes());
                                workTimeService.save(userWorkTime);

                                WorkDay todayWorkDay = Optional.ofNullable(workDayRepository.findByDateAndWorkTime(LocalDate.now(), userWorkTime))
                                        .orElse(new WorkDay(0L, 0L, userWorkTime));
                                todayWorkDay.setIdleMinutes(diff > 0 ? diff : todayWorkDay.getIdleMinutes());
                                workDayRepository.save(todayWorkDay);

//                                periodService.save(new DowntimePeriod(startTime, LocalDateTime.now(), todayWorkDay));
                        }));

        return true;
    }

    @Override
    public boolean isClientSettingsUpdated(long settingsVersion) {
        LOG.debug("Client settings version {}", settingsVersion);
        ClientSettings clientSettings = clientSettingsService.getAll().stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        int dbSettingsVersion = clientSettings.getSettingsVersion();
        LOG.debug("DB settings version {}", dbSettingsVersion);
        return dbSettingsVersion != settingsVersion;
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
        User user = userService.getByTrackerToken(timeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("getTodayWorkTime: username {}", user.getUsername());
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        LOG.debug("getTodayWorkTime: projectID {}", project.getId());
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime());
        LocalDateTime startedWork = userWorkTime.getStartedWork();
        Long todayMinutes = userWorkTime.getTodayMinutes();
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
        User user = userService.getByTrackerToken(timeModel.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("getTotalWorkTime: username {}", user.getUsername());
        Project project = projectService.getById(timeModel.getProjectId()).orElseThrow(NoSuchElementException::new);
        LOG.debug("getTotalWorkTime: projectID {}", project.getId());
        WorkTime totalWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime());
        Long totalMinutes = totalWorkTime.getTotalMinutes();
        Long hours = totalMinutes / 60;
        Long minutes = totalMinutes - hours * 60;
        Long downtime = totalWorkTime.getDowntimeMinutes();
        double downtimePercent = Math.floor(downtime * 100 / Double.valueOf(totalMinutes) * 100) / 100;
        LOG.debug("getTotalWorkTime: hours {}, minutes {}, downtime {}%", hours, minutes, downtimePercent);
        return new TimeModel(hours, minutes, downtimePercent);
    }

    @Override
    public void saveKeyboardLog(WriteKeyboard writeKeyboard) {
        LOG.debug("saveKeyboardLog: {}", writeKeyboard);
        User user = userService.getByTrackerToken(writeKeyboard.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("saveKeyboardLog: username {}", user.getUsername());
        Project project = projectService.getById(writeKeyboard.getProjectID()).orElseThrow(NoSuchElementException::new);
        LOG.debug("saveKeyboardLog: projectID {}", project.getId());
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(null, project, user));
        LOG.info("saveKeyboardLog: {}", workTime);
        metricsService.save(new Keyboard(writeKeyboard.getWords(), workTime));
    }

    @Override
    public void saveScreenshot(ScreenshotModel file) {
        LOG.debug("saveScreenshot: {}", file);
        User user = userService.getByTrackerToken(file.getToken()).orElseThrow(NoSuchElementException::new);
        LOG.debug("saveScreenshot: username {}", user.getUsername());
        Project project = projectService.getById(file.getProjectID()).orElseThrow(NoSuchElementException::new);
        LOG.debug("saveScreenshot: projectID {}", project.getId());
        WorkTime workTime = workTimeService.getWorkTimeOfUserAndProject(user, project).orElse(new WorkTime(null, project, user));
        LOG.info("saveScreenshot: {}", workTime);
        try {
            String filePath = CWD + environment.getProperty("screenshot.path") + System.currentTimeMillis() + "." + FILE_EXTENSION;
            File screenshotFile = new File(filePath);
            screenshotFile.getParentFile().mkdirs();
            ImageIO.write(ImageIO.read(new ByteArrayInputStream(file.getFile())), FILE_EXTENSION, screenshotFile);
            metricsService.save(new Screenshot(filePath, workTime));
        } catch (Exception e) {
            LOG.error("Failed to save screenshot", e);
        }
    }

    private ClientSettingsModel convertClientSettingsToModel(ClientSettings settings) {
        return new ClientSettingsModel(
                settings.getSettingsVersion(),
                settings.getScreenshotUpdateFrequentlyInMinutes(),
                settings.getKeyboardUpdateFrequentlyInMinutes(),
                settings.getStartDowntimeAfterInMinutes());
    }
}
