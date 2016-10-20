package ua.softgroup.matrix.server.api;

import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.security.TokenAuthService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MatrixServerApiImpl implements MatrixServerApi {

    private TokenAuthService tokenAuthService = new TokenAuthService();

    @Override
    public String authenticate(String login, String password) {
        System.out.println("- service layer: authenticate: " + login + " " + password + "\n");
        return tokenAuthService.authenticate(login, password);
    }

    @Override
    public List<Object> getAllProjects() {
        return null;
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
            File outputfile = new File("image.png");
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("saved");
    }
}
