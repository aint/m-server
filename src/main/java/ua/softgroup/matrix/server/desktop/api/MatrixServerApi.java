package ua.softgroup.matrix.server.desktop.api;

import ua.softgroup.matrix.api.model.datamodels.AuthModel;
import ua.softgroup.matrix.api.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.api.model.datamodels.InitializeModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.datamodels.ReportsContainerDataModel;
import ua.softgroup.matrix.api.model.datamodels.SynchronizationModel;
import ua.softgroup.matrix.api.model.datamodels.TimeModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;

@SuppressWarnings("rawtypes")
public interface MatrixServerApi {

    ResponseModel<InitializeModel> authenticate(RequestModel<AuthModel> authModel);

    ResponseModel<ReportsContainerDataModel> getProjectReports(RequestModel requestModel);

    ResponseModel saveReport(RequestModel<ReportModel> reportModel);

    ResponseModel startWork(RequestModel requestModel);

    ResponseModel endWork(RequestModel requestModel);

    ResponseModel<TimeModel> processCheckpoint(RequestModel<CheckPointModel> requestModel);

    ResponseModel syncCheckpoints(RequestModel<SynchronizationModel> requestModel);

}
