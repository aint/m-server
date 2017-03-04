package ua.softgroup.matrix.server.desktop.api;

import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportsContainerDataModel;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;

public interface MatrixServerApi {

    /**
     * Tries to authenticate a user using the given credentials
     *
     * @param authModel DTO with username and password
     * @return a token in the case of successful authentication, 'invalid credentials' otherwise
     */
    ResponseModel<InitializeModel> authenticate(AuthModel authModel);

    ResponseModel<ReportsContainerDataModel> getProjectReports(RequestModel requestModel);

    ResponseModel saveReport(RequestModel<ReportModel> reportModel);

    ResponseModel startWork(RequestModel requestModel);

    ResponseModel endWork(RequestModel requestModel);

//    Set<ProjectModel> getUserActiveProjects(TokenModel tokenModel);
//
//
//    Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId);
//
//
//    void startDowntime(TimeModel downTimeModel);
//
//    void endDowntime(TimeModel downTimeModel);
//
//    TimeModel getTodayWorkTime(TimeModel timeModel);
//
//    TimeModel getTotalWorkTime(TimeModel timeModel);
//
//    ClientSettingsModel getClientSettings();
//
//    void saveKeyboardLog(WriteKeyboard writeKeyboard);
//
//    void saveActiveWindowsLog(ActiveWindowsModel activeWindows);
//
//    void saveScreenshot(ScreenshotModel file);

}
