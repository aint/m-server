package ua.softgroup.matrix.server.desktop.api;

import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportsContainerDataModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.TimeModel;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.persistent.entity.ClientSettings;
import ua.softgroup.matrix.server.service.ClientSettingsService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;

import java.util.NoSuchElementException;

import static ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus.SUCCESS;

@SuppressWarnings("rawtypes")
@Service
public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private final UserService userService;
    private final ProjectService projectService;
    private final WorkDayService workDayService;
    private final ClientSettingsService clientSettingsService;
    private final TrackingService trackingService;

    @Autowired
    public MatrixServerApiImpl(UserService userService,
                               ProjectService projectService,
                               WorkDayService workDayService, ClientSettingsService clientSettingsService,
                               TrackingService trackingService) {
        this.userService = userService;
        this.projectService = projectService;
        this.workDayService = workDayService;
        this.clientSettingsService = clientSettingsService;
        this.trackingService = trackingService;
    }

    @Override
    public ResponseModel<InitializeModel> authenticate(RequestModel<AuthModel> authrequestModel) {
        LOG.info("Authenticate {}, {}", authrequestModel);

        AuthModel authModel = authrequestModel.getDataContainer().or(throwException());
        String token = userService.authenticate(authModel);
        if (token == null) {
            return new ResponseModel<>(ResponseStatus.INVALID_CREDENTIALS);
        }
        ClientSettings clientSettings = clientSettingsService.getAll().stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        InitializeModel initializeModel = new InitializeModel(
                token,
                projectService.getUserActiveProjects(token),
                clientSettings.getStartDowntimeAfterInMinutes(),
                clientSettings.getScreenshotUpdateFrequentlyInMinutes(),
                10);
        return new ResponseModel<>(initializeModel);
    }

    @Override
    public ResponseModel<ReportsContainerDataModel> getProjectReports(RequestModel requestModel) {
        Long projectId = requestModel.getProjectId();
        String token = requestModel.getToken();

        return new ResponseModel<>(new ReportsContainerDataModel(workDayService.getWorkDaysOf(token, projectId)));
    }

    @Override
    public ResponseModel saveReport(RequestModel<ReportModel> reportRequestModel) {
        ReportModel reportModel = reportRequestModel.getDataContainer().or(throwException());
        String token = reportRequestModel.getToken();

        return new ResponseModel<>(workDayService.saveReportOrUpdate(token, reportModel));
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

    @Override
    public ResponseModel<TimeModel> processCheckpoint(RequestModel<CheckPointModel> requestModel) {
        CheckPointModel checkPointModel = requestModel.getDataContainer().or(throwException());

        trackingService.saveTrackingData(
                requestModel.getProjectId(),
                checkPointModel.getKeyboardLogs(),
                checkPointModel.getMouseFootage(),
                checkPointModel.getWindowsTimeMap(),
                checkPointModel.getScreenshot());


        TimeModel timeModel = projectService.saveCheckpointTime(requestModel.getProjectId(), (int) checkPointModel.getIdleTime());
        return new ResponseModel<>(timeModel);
    }

    private Supplier throwException() {
        return () -> {
            throw new RuntimeException("Data container is empty");
        };
    }

}
