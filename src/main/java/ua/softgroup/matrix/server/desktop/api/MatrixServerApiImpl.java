package ua.softgroup.matrix.server.desktop.api;

import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.softgroup.matrix.api.model.datamodels.AuthModel;
import ua.softgroup.matrix.api.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.api.model.datamodels.InitializeModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.datamodels.ReportsContainerDataModel;
import ua.softgroup.matrix.api.model.datamodels.TimeModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.service.TrackerSettingsService;
import ua.softgroup.matrix.server.service.ProjectService;
import ua.softgroup.matrix.server.service.TrackingService;
import ua.softgroup.matrix.server.service.UserService;
import ua.softgroup.matrix.server.service.WorkDayService;

@SuppressWarnings("rawtypes")
@Service
public class MatrixServerApiImpl implements MatrixServerApi {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixServerApiImpl.class);

    private final UserService userService;
    private final ProjectService projectService;
    private final WorkDayService workDayService;
    private final TrackerSettingsService trackerSettingsService;
    private final TrackingService trackingService;

    @Autowired
    public MatrixServerApiImpl(UserService userService,
                               ProjectService projectService,
                               WorkDayService workDayService,
                               TrackerSettingsService trackerSettingsService,
                               TrackingService trackingService) {
        this.userService = userService;
        this.projectService = projectService;
        this.workDayService = workDayService;
        this.trackerSettingsService = trackerSettingsService;
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
        TrackerSettingsService.TrackerSettings trackerSettings = trackerSettingsService.getTrackerSettings(token);
        InitializeModel initializeModel = new InitializeModel(
                token,
                projectService.getUserActiveProjects(token),
                trackerSettings.getStartIdleAfterSeconds(),
                trackerSettings.getScreenshotPeriodFrequency(),
                trackerSettings.getCheckpointFrequencyInSeconds());
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
        Long projectId = reportRequestModel.getProjectId();

        return new ResponseModel<>(workDayService.saveReportOrUpdate(token, projectId, reportModel));
    }

    @Override
    public ResponseModel startWork(RequestModel requestModel) {
        String token = requestModel.getToken();
        Long projectId = requestModel.getProjectId();

        TimeModel timeModel = projectService.saveStartWorkTime(token, projectId);
        return new ResponseModel<>(timeModel);
    }

    @Override
    public ResponseModel endWork(RequestModel requestModel) {
        String token = requestModel.getToken();
        Long projectId = requestModel.getProjectId();

        return new ResponseModel<>(projectService.saveEndWorkTime(token, projectId));
    }

    @Override
    public ResponseModel<TimeModel> processCheckpoint(RequestModel<CheckPointModel> requestModel) {
        CheckPointModel checkPointModel = requestModel.getDataContainer().or(throwException());

        trackingService.saveTrackingData(
                requestModel.getToken(),
                requestModel.getProjectId(),
                checkPointModel.getKeyboardLogs(),
                checkPointModel.getMouseFootage(),
                checkPointModel.getWindowsTimeMap(),
                checkPointModel.getScreenshot());


        TimeModel timeModel = projectService.saveCheckpointTime(requestModel.getToken(), requestModel.getProjectId(), checkPointModel.getIdleTime());
        return new ResponseModel<>(timeModel);
    }

    private Supplier throwException() {
        return () -> {
            throw new RuntimeException("Data container is empty");
        };
    }

}
