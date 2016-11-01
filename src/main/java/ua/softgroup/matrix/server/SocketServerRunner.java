package ua.softgroup.matrix.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.api.MatrixServerApi;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.model.ProjectModel;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.SynchronizedModel;
import ua.softgroup.matrix.server.model.TimeModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.model.UserPassword;
import ua.softgroup.matrix.server.model.WriteKeyboard;
import ua.softgroup.matrix.server.persistent.entity.Project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Component
public class SocketServerRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServerRunner.class);

    private final MatrixServerApi matrixServerApi;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    @Autowired
    public SocketServerRunner(MatrixServerApi matrixServerApi) {
        this.matrixServerApi = matrixServerApi;
    }

    @Override
    public void run(String... args) throws Exception {
        createServerSocket();
        LOG.info("Waiting for a client...");

        while (true) {
            acceptClientSocket();
            LOG.info("Client connected");

            openObjectInputStream();
            openDataOutputStream();
            openDataInputStream();

            ServerCommands command;
            while (!clientRequestClose(command = readServerCommand())) {
                LOG.info("Client entered command {}", command.name());

                processClientInput(command);
            }
        }
    }

    private int readServerPortFromConfig() throws IOException {
        InputStream in = SocketServerRunner.class.getClassLoader().getResourceAsStream("server.properties");
        Properties prop = new Properties();
        prop.load(in);
        return Integer.valueOf(prop.getProperty("port"));

    }

    private void createServerSocket() throws IOException {
        serverSocket = new ServerSocket(readServerPortFromConfig());
    }

    private void acceptClientSocket() throws IOException {
        clientSocket = serverSocket.accept();
    }

    private void openObjectInputStream() throws IOException {
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void openDataOutputStream() throws IOException {
        dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
    }

    private void sendStringResponse(String text) throws IOException {
        dataOutputStream.writeUTF(text);
        dataOutputStream.flush();
//        out.close();
    }

    private ServerCommands readServerCommand() throws IOException, ClassNotFoundException {
        return (ServerCommands) objectInputStream.readObject();
    }

    private boolean clientRequestClose(ServerCommands command) throws IOException {
        if ((ServerCommands.CLOSE == command)) {
            closeClientSocket();
            LOG.info("Client closed connection");
            LOG.info("-----------------------");
            return true;
        }
        return false;
    }

    private void closeClientSocket() throws IOException {
        dataOutputStream.close();
        objectInputStream.close();
        clientSocket.close();
    }

    private void processClientInput(ServerCommands command) throws IOException, ClassNotFoundException {
        if (ServerCommands.AUTHENTICATE == command) {
            UserPassword auth = (UserPassword) objectInputStream.readObject();
            String token = matrixServerApi.authenticate(auth.getUsername(), auth.getPassword());
            sendStringResponse(token);
        } else if (ServerCommands.GET_ALL_PROJECT == command) {
            TokenModel token = (TokenModel) objectInputStream.readObject();
//            sendAllObjectsToClient(matrixServerApi.getAllProjects(token));
            Set<Project> userActiveProjects = matrixServerApi.getUserActiveProjects(token);
            Set<ProjectModel> set = new HashSet<>();
            for (Project project : userActiveProjects) {
                LOG.warn("PROJECT {}", project);
                ProjectModel projectModel = new ProjectModel();
                projectModel.setAuthorName(project.getAuthorName());
                projectModel.setTitle(project.getTitle());
                projectModel.setDescription(project.getDescription());
                projectModel.setEndDate(project.getEndDate());
                projectModel.setId(project.getId());
                projectModel.setRate(project.getRate());
                projectModel.setRateCurrencyId(project.getRateCurrencyId());
                projectModel.setStartDate(project.getStartDate());
                set.add(projectModel);
            }
            sendAllObjectsToClient(set);
        } else if (ServerCommands.SET_CURRENT_PROJECT == command) {
            matrixServerApi.setCurrentProject(0L);
        } else if (ServerCommands.GET_REPORT == command) {
            ReportModel reportRequest = (ReportModel) objectInputStream.readObject();
            sendReportToClient(matrixServerApi.getReport(reportRequest));
        } else if (ServerCommands.SAVE_REPORT == command) {
            ReportModel report = (ReportModel) objectInputStream.readObject();
            sendConstantStatus(matrixServerApi.saveReport(report).name());
        } else if (ServerCommands.SAVE_SCREENSHOT == command) {
            ScreenshotModel file = (ScreenshotModel) objectInputStream.readObject();
            matrixServerApi.saveScreenshot(file);
        } else if (ServerCommands.GET_ALL_REPORTS == command) {
            TokenModel token = (TokenModel) objectInputStream.readObject();
            sendAllObjectsToClient(matrixServerApi.getAllReports(token));
        } else if (ServerCommands.GET_REPORTS_BY_PROJECT_ID == command) {
            TokenModel token = (TokenModel) objectInputStream.readObject();
            long id = dataInputStream.readLong();
            sendAllObjectsToClient(matrixServerApi.getAllReportsByProjectId(token, id));
        } else if (ServerCommands.START_WORK == command) {
            TimeModel timeModel = (TimeModel) objectInputStream.readObject();
            matrixServerApi.startWork(timeModel);
        } else if (ServerCommands.END_WORK == command) {
            TimeModel token = (TimeModel) objectInputStream.readObject();
            matrixServerApi.endWork(token);
        } else if (ServerCommands.START_DOWNTIME == command) {
            TimeModel timeModel = (TimeModel) objectInputStream.readObject();
            matrixServerApi.startDowntime(timeModel);
        } else if (ServerCommands.STOP_DOWNTIME == command) {
            TimeModel token = (TimeModel) objectInputStream.readObject();
            matrixServerApi.endDowntime(token);
        } else if (ServerCommands.SYNCHRONIZED == command) {
            boolean syncFlag = matrixServerApi.sync((Set<SynchronizedModel>) objectInputStream.readObject());
            dataOutputStream.writeBoolean(syncFlag);
            dataOutputStream.flush();
        } else if (ServerCommands.GET_TODAY_TIME == command) {
            TimeModel workTime = matrixServerApi.getTodayWorkTime((TimeModel) objectInputStream.readObject());
            sendAllObjectsToClient(workTime);
        } else if (ServerCommands.GET_TOTAL_TIME == command) {
            TimeModel workTime = matrixServerApi.getTotalWorkTime((TimeModel) objectInputStream.readObject());
            sendAllObjectsToClient(workTime);
        } else if (ServerCommands.CHECK_UPDATE_SETTING == command) {
            long version = dataInputStream.readLong();
            dataOutputStream.writeBoolean(matrixServerApi.isClientSettingsUpdated(version));
            dataOutputStream.flush();
        } else if (ServerCommands.UPDATE_SETTING == command) {
            sendAllObjectsToClient(matrixServerApi.getClientSettings());
        } else if (ServerCommands.KEYBOARD_LOG == command) {
            matrixServerApi.saveKeyboardLog((WriteKeyboard) objectInputStream.readObject());
        } else if (ServerCommands.CLOSE == command) {
            closeClientSocket();
        } else {
            LOG.warn("No such command {}", command);
        }
    }

    private void openDataInputStream() throws IOException {
        dataInputStream = new DataInputStream(clientSocket.getInputStream());
    }


    private void sendAllObjectsToClient(Object reports) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(reports);
        out.flush();
    }

    private void sendReportToClient(ReportModel report) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(report);
        out.flush();
    }

    private void sendConstantStatus(String status) throws IOException {
        dataOutputStream.writeUTF(status);
        dataOutputStream.flush();
    }



}
