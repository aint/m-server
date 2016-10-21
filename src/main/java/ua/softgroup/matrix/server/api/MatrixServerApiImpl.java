package ua.softgroup.matrix.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.persistent.entity.Project;
import ua.softgroup.matrix.server.persistent.entity.Report;
import ua.softgroup.matrix.server.persistent.entity.User;
import ua.softgroup.matrix.server.security.TokenAuthService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.ReportService;
import ua.softgroup.matrix.server.service.UserService;
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
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private LocalDateTime workTime;

    private TokenAuthService tokenAuthService = new TokenAuthService();

    private UserService userService = new UserServiceImpl();
    private ReportService reportService = new ReportServiceImpl();
    private ProjectService projectService = new ProjectServiceImpl();

    @Override
    public String authenticate(String login, String password) {
        LOG.info("authenticate: {}, {}", login, password);
//        System.out.println("- service layer: authenticate: " + login + " " + password + "\n");
        return tokenAuthService.authenticate(login, password);
    }

    @Override
    public Constants saveReport(ReportModel reportModel) {
        LOG.info("saveReport: {}", reportModel);
//        System.out.println("- service layer: saveReport: " + reportModel.getTitle() + reportModel.getDiscription() + "\n");
//        System.out.println(reportModel);

        if (!isTokenValidated(reportModel.getToken())) {
            return Constants.TOKEN_EXPIRED;
        }

        Report report = new Report(reportModel.getTitle(), reportModel.getDiscription(), retrieveUserFromToken(reportModel));
        report.setId(reportModel.getId());
        report.setProject(projectService.getById(reportModel.getProjectId()));
        reportService.save(report);

        return Constants.TOKEN_VALIDATED;
    }

    @Deprecated
    @Override
    public ReportModel getReport(ReportModel reportModel) {
        LOG.info("getReport: {}", reportModel);
//        System.out.println("- service layer: getReport: " + reportModel.getId() + "\n");
//        System.out.println(reportModel);

        Report report = reportService.getById(reportModel.getId());
        System.out.println(report);

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
            e.printStackTrace();
        }
        System.out.println("saved");
    }

    @Override
    public void startWork(TokenModel tokenModel) {
        workTime = LocalDateTime.now();
    }

    @Override
    public void endWork(TokenModel tokenModel) {
        Duration duration = Duration.between(workTime, LocalDateTime.now());
        System.out.println(duration.toDays());
        System.out.println(duration.toHours());
        System.out.println(duration.toMillis());
    }
}
