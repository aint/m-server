package ua.softgroup.matrix.server.api;

import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.security.TokenAuthService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatrixServerApiImpl implements MatrixServerApi {

    private LocalDateTime workTime;

    private TokenAuthService tokenAuthService = new TokenAuthService();

    @Override
    public String authenticate(String login, String password) {
        System.out.println("- service layer: authenticate: " + login + " " + password + "\n");
        return tokenAuthService.authenticate(login, password);
    }

    @Override
    public Set<ProjectModel> getAllProjects(TokenModel tokenModel) {
        String token = tokenModel.getToken();
        // TODO extract user
        ProjectModel pr1 = new ProjectModel("Project 1", "Description 1", 111.111);
        ProjectModel pr2 = new ProjectModel("Project 2", "Description 2", 222.222);
        ProjectModel pr3 = new ProjectModel("Project 3", "Description 3", 333.333);
        return new HashSet<>(Arrays.asList(pr1, pr2, pr3));
    }

    @Override
    public void setCurrentProject(Long projectId) {

    }

    @Override
    public ReportModel getReport(ReportModel reportModel) {
        System.out.println("- service layer: getReport: " + reportModel.getId() + "\n");

        reportModel.setStatus(0);
        if (tokenAuthService.validateToken(reportModel.getToken()) == Constants.TOKEN_EXPIRED) {
            reportModel.setStatus(-1);
        }

        reportModel.setDiscription("Test description");
        reportModel.setTitle("Test Title");

        return reportModel;
    }

    @Override
    public Constants saveReport(ReportModel reportModel) {
        System.out.println("- service layer: saveReport: " + reportModel.getTitle() + reportModel.getDiscription() + "\n");
        System.out.println("- service layer: saveReport: " + reportModel.getToken());
        return tokenAuthService.validateToken(reportModel.getToken());
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
    public Set<ReportModel> getAllReports(TokenModel tokenModel) {
        String token = tokenModel.getToken();
        // TODO extract user
        ReportModel r1 = new ReportModel(token, "Title 1", "Description 1");
        ReportModel r2 = new ReportModel(token, "Title 2", "Description 2");
        ReportModel r3 = new ReportModel(token, "Title 3", "Description 3");
        return new HashSet<>(Arrays.asList(r1, r2, r3));
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
