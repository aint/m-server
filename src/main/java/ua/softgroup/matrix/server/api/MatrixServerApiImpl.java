package ua.softgroup.matrix.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.model.*;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.security.TokenAuthService;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.impl.ClientSettingsServiceImpl;
import ua.softgroup.matrix.server.service.impl.ProjectServiceImpl;
import ua.softgroup.matrix.server.service.impl.ReportServiceImpl;
import ua.softgroup.matrix.server.service.impl.UserServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private static final int REPORT_EDIT_MAX_PERIOD_DAYS = 1;

    private LocalDateTime workTime;

    private TokenAuthService tokenAuthService = new TokenAuthService();

    private UserService userService = new UserServiceImpl();
    private ReportService reportService = new ReportServiceImpl();
    private ProjectService projectService = new ProjectServiceImpl();
    private ClientSettingsService clientSettingsService = new ClientSettingsServiceImpl();

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
        report.setDescription(reportModel.getDiscription());
        report.setAuthor(retrieveUserFromToken(reportModel));
        report.setProject(projectService.getById(reportModel.getProjectId()));
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
        reportModel.setDiscription(report.getDescription());
        reportModel.setStatus(isTokenValidated(reportModel.getToken()) ? 0 : -1);
        return reportModel;
    }

    @Override
    public Set<ReportModel> getAllReports(TokenModel tokenModel) {
        String token = tokenModel.getToken();
        return reportService.getAllReportsOf(retrieveUserFromToken(tokenModel)).stream()
                .map(r -> new ReportModel(r.getId(), token, r.getTitle(), r.getDescription(), r.getProject().getId()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ProjectModel> getAllProjects(TokenModel tokenModel) {
//        User user = retrieveUserFromToken(tokenModel);
        return projectService.getAll().stream()
                .map(p -> new ProjectModel(p.getId(), p.getName(), p.getDescription(), p.getTotalPrice()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId) {
        LOG.debug("Requested project id {}", projectId);
        String token = tokenModel.getToken();
        return projectService.getById(projectId).getReports().stream()
                .map(report ->  new ReportModel(token, report.getTitle(), report.getDescription()))
                .collect(Collectors.toCollection(HashSet::new));
    }



    private User retrieveUserFromToken(TokenModel tokenModel) {
        String username = tokenAuthService.extractUsername(tokenModel);
        return userService.getByUsername(username);
    }

    private boolean isTokenValidated(String token) {
        return tokenAuthService.validateToken(token) == Constants.TOKEN_VALIDATED;
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
    public void startWork(TokenModel tokenModel) {
        workTime = LocalDateTime.now();
    }

    @Override
    public void endWork(TokenModel tokenModel) {
        Duration duration = Duration.between(workTime, LocalDateTime.now());
        LOG.debug("Work period in days {}", duration.toDays());
        LOG.debug("Work period in hours {}", duration.toHours());
        LOG.debug("Work period in millis {}", duration.toMillis());
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
                settings.getKeyboardUpdateFrequently());
    }
}
