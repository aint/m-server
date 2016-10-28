package ua.softgroup.matrix.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.model.ClientSettingsModel;
import ua.softgroup.matrix.server.model.DownTimeModel;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.SynchronizedModel;
import ua.softgroup.matrix.server.model.TimeModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.entity.Downtime;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.TimePeriod;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.persistent.entity.WorkTime;
import ua.softgroup.matrix.server.security.TokenAuthService;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.DowntimeService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.TimePeriodService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkTimeService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private static final int REPORT_EDIT_MAX_PERIOD_DAYS = 5;

    @Autowired
    private TokenAuthService tokenAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ClientSettingsService clientSettingsService;
    @Autowired
    private WorkTimeService workTimeService ;
    @Autowired
    private TimePeriodService timePeriodService;
    @Autowired
    private DowntimeService downtimeService;

    @Override
    public String authenticate(String login, String password) {
        LOG.debug("authenticate: {}, {}", login, password);
        return tokenAuthService.authenticate(login, password);
    }

    @Override
    public Constants saveReport(ReportModel reportModel) {
        LOG.debug("saveReport: {}", reportModel);

        if (!isTokenValidated(reportModel.getToken())) {
            return Constants.TOKEN_EXPIRED;
        }

        Report report = reportService.getById(reportModel.getId());
        if (report != null) {
            return updateReport(report, reportModel);
        }

        persistReport(new Report(reportModel.getId()), reportModel);

        return Constants.TOKEN_VALIDATED;
    }

    private Constants updateReport(Report report, ReportModel reportModel) {
        LOG.debug("updateReport");
        Duration duration = Duration.between(report.getCreationDate(), LocalDateTime.now());
        LOG.debug("Report created {} hours ago", duration.toHours());
        //TODO change to hours
        if (duration.toMinutes() > REPORT_EDIT_MAX_PERIOD_DAYS) {
            LOG.debug("Report expired");
            return Constants.REPORT_EXPIRED;
        }
        persistReport(report, reportModel);

        return Constants.TOKEN_VALIDATED;
    }

    private void persistReport(Report report, ReportModel reportModel) {
        report.setTitle(reportModel.getTitle());
        report.setDescription(reportModel.getDescription());
        report.setAuthor(retrieveUserFromToken(reportModel));
        Project proj = projectService.getById(reportModel.getProjectId());
        LOG.warn("proj id {}", reportModel.getProjectId());
        LOG.warn("proj {}", proj);
        report.setProject(proj);
        reportService.save(report);
    }

    @Deprecated
    @Override
    public ReportModel getReport(ReportModel reportModel) {
        LOG.debug("getReport: {}", reportModel);

        Report report = reportService.getById(reportModel.getId());
        LOG.debug("getReport: {}", report);

        reportModel.setId(report.getId());
        reportModel.setTitle(report.getTitle());
        reportModel.setDescription(report.getDescription());
        reportModel.setStatus(isTokenValidated(reportModel.getToken()) ? 0 : -1);
        reportModel.setChecked(report.isChecked());
        return reportModel;
    }

    @Override
    public Set<ReportModel> getAllReports(TokenModel tokenModel) {
        String token = tokenModel.getToken();
        return reportService.getAllReportsOf(retrieveUserFromToken(tokenModel)).stream()
                .map(report -> convertReportEntityToModel(report, tokenModel.getToken()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private ReportModel convertReportEntityToModel(Report report, String token) {
        ReportModel reportModel = new ReportModel();
        reportModel.setToken(token);
        reportModel.setId(report.getId());
        reportModel.setTitle(report.getTitle());
        reportModel.setDescription(report.getDescription());
        reportModel.setProjectId(report.getProject().getId());
        reportModel.setChecked(report.isChecked());
        return reportModel;
    }

    @Override
    public Set<ProjectModel> getAllProjects(TokenModel tokenModel) {
//        User user = retrieveUserFromToken(downtimeModel);
        return null;
//                projectService.getAll().stream()
//                .map(p -> new ProjectModel(p.getId(), p.getTitle(), p.getDescription(), p.getRate()))
//                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Project> getUserActiveProjects(TokenModel tokenModel) {
        return projectService.getUserActiveProjects(tokenModel.getToken());
//        return projectService.getUserActiveProjects(tokenModel.getToken()).stream()
//                .map(p -> new ProjectModel(p.getId(), p.getTitle(), p.getDescription(), p.getRate()))
//                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId) {
        LOG.debug("Requested project id {}", projectId);
        User user = retrieveUserFromToken(tokenModel);
        Project project = projectService.getById(projectId);
        return reportService.getAllReportsOf(user, project).stream()
                .map(report -> new ReportModel(report.getId(), tokenModel.getToken(), report.getTitle(), report.getDescription(), projectId))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private User retrieveUserFromToken(TokenModel tokenModel) {
//        String username = tokenAuthService.extractUsername(downtimeModel);
//        return userService.getByUsername(username);
        User user = userService.getByTrackerToken(tokenModel.getToken());
        if (user == null) {
            throw new RuntimeException("User can't be null");
        }
        return user;
    }

    private boolean isTokenValidated(String token) {
        return true;
//        return tokenAuthService.validateToken(token) == Constants.TOKEN_VALIDATED;
    }

    @Override
    public void setCurrentProject(Long projectId) {

    }

    @Override
    public void saveScreenshot(ScreenshotModel file) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(file.getFile()));
            File outputFile = new File("image.png");
            ImageIO.write(img, "png", outputFile);
        } catch (IOException e) {
            LOG.error("Failed to save screenshot", e);
        }
    }

    @Override
    public void startWork(TimeModel timeModel) {
        LOG.debug("TimeModel {} ", timeModel);
        User user = retrieveUserFromToken(timeModel);
        LOG.debug("User {} start work", user);
        Project project = projectService.getById(timeModel.getProjectId());
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project);
        if (userWorkTime != null) {
            userWorkTime.setStartedWork(LocalDateTime.now());
        } else {
            userWorkTime = new WorkTime(LocalDateTime.now(), projectService.getById(timeModel.getProjectId()), user);
        }
        workTimeService.save(userWorkTime);
    }

    @Override
    public void endWork(TimeModel timeModel) {
        LOG.debug("TimeModel {}", timeModel);
        User user = retrieveUserFromToken(timeModel);
        LOG.debug("User {} end work", user);
        Project project = projectService.getById(timeModel.getProjectId());
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project);
        if (userWorkTime != null) {
            LocalDateTime startedWork = userWorkTime.getStartedWork();
            if (startedWork != null) {
                Duration duration = Duration.between(startedWork, LocalDateTime.now());
                LOG.debug("Work period in minutes {}", duration.toMinutes());
                LOG.debug("Work period in millis {}", duration.toMillis());
                userWorkTime.setStartedWork(null);
                // TODO perefer server time
                if (timeModel.getMinute() != duration.toMinutes()) {
                    userWorkTime.setTotalMinutes(userWorkTime.getTotalMinutes() + (int) timeModel.getMinute());
                } else {
                    userWorkTime.setTotalMinutes(userWorkTime.getTotalMinutes() + (int) duration.toMinutes());
                }
                workTimeService.save(userWorkTime);
                timePeriodService.save(new TimePeriod(startedWork, LocalDateTime.now(), userWorkTime));
            }
        }
    }

    @Override
    public void startDowntime(DownTimeModel downtimeModel) {
        LOG.debug("startDowntime DownTimeModel {}", downtimeModel);
        User user = retrieveUserFromToken(downtimeModel);
        LOG.debug("startDowntime User {}", user);
        Project project = projectService.getById(downtimeModel.getProjectId());
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project);
        if (userWorkTime != null) {
            Downtime downtime = userWorkTime.getDowntime();
            if (downtime == null) {
                downtime = new Downtime(userWorkTime);
            }
            downtime.setStartTime(LocalDateTime.now());
            downtimeService.save(downtime);
        }
    }

    @Override
    public void endDowntime(DownTimeModel downtimeModel) {
        LOG.debug("endDowntime DownTimeModel {}", downtimeModel);
        User user = retrieveUserFromToken(downtimeModel);
        LOG.debug("endDowntime User {}", user);
        Project project = projectService.getById(downtimeModel.getProjectId());
        WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project);
        if (userWorkTime != null) {
            Downtime downtime = userWorkTime.getDowntime();
            if (downtime != null) {
                Duration duration = Duration.between(downtime.getStartTime(), LocalDateTime.now());
                LOG.debug("Downtime in minutes {}", duration.toMinutes());
                LOG.debug("Downtime in millis {}", duration.toMillis());
                downtime.setMinutes(downtime.getMinutes() + duration.toMinutes());
                downtime.setStartTime(null);
                downtimeService.save(downtime);
            }
        }
    }

    private boolean checkIsSyncModelTheSame(SynchronizedModel synchronizedModel) {
        SynchronizedModel syncFile = null;
        final String fileName = "sync.ser";
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            syncFile = (SynchronizedModel) in.readObject();
            if (synchronizedModel.equals(syncFile)) {
                return true;
            }
            serializableFile(synchronizedModel);
            in.close();
            fileIn.close();
        } catch (FileNotFoundException fnfe) {
            serializableFile(synchronizedModel);
        } catch(Exception e) {
            LOG.error("checkIsSyncModelExist", e);
            return true;
        }
        return false;
    }

    private void serializableFile(SynchronizedModel synchronizedModel) {
        final String fileName = "sync.ser";
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(synchronizedModel);
            out.close();
            fileOut.close();
            LOG.debug("Serialized sync model {}", synchronizedModel);
        }catch(IOException e) {
            LOG.error("checkIsSyncModelExist", e);
        }
    }

    @Override
    public void sync(SynchronizedModel synchronizedModel) {
        LOG.warn("Sync {}", synchronizedModel);

        if (checkIsSyncModelTheSame(synchronizedModel)) {
            return;
        }

        ReportModel reportModel = synchronizedModel.getReportModel();
        if (reportModel != null) {
            saveReport(reportModel);
        }
        TimeModel timeModel = synchronizedModel.getTimeModel();
        if (timeModel != null) {
            User user = retrieveUserFromToken(timeModel);
            Project project = projectService.getById(timeModel.getProjectId());
            WorkTime userWorkTime = workTimeService.getWorkTimeOfUserAndProject(user, project);
            LOG.debug("WorkTime username {}, projectId {} ", user.getUsername(), project.getId());
            if (userWorkTime == null) {
                userWorkTime = new WorkTime(null, project, user);
            }
            userWorkTime.setTotalMinutes(userWorkTime.getTotalMinutes() + (int) timeModel.getMinute());
            workTimeService.save(userWorkTime);
        }
    }

    @Override
    public boolean isClientSettingsUpdated(long settingsVersion) {
        LOG.debug("Client settings version {}", settingsVersion);
        ClientSettings clientSettings = clientSettingsService.getAll().stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new); //TODO set default settings
        int dbSettingsVersion = clientSettings.getSettingsVersion();
        LOG.debug("DB settings version {}", dbSettingsVersion);
        return dbSettingsVersion > settingsVersion;
    }

    @Override
    public ClientSettingsModel getClientSettings() {
        return clientSettingsService.getAll().stream()
                .findFirst()
                .map(this::convertClientSettingsToModel)
                .orElseThrow(NoSuchElementException::new); //TODO set default settings
    }

    private ClientSettingsModel convertClientSettingsToModel(ClientSettings settings) {
        return new ClientSettingsModel(
                settings.getSettingsVersion(),
                settings.getScreenshotUpdateFrequently(),
                settings.getKeyboardUpdateFrequently(),
                settings.getStartDowntimeAfter());
    }
}
