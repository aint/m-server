package ua.softgroup.matrix.server.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.config.LoadDefaultConfig;
import ua.softgroup.matrix.server.desktop.api.MatrixServerApi;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.desktop.model.ScreenshotModel;
import ua.softgroup.matrix.server.desktop.model.SynchronizedModel;
import ua.softgroup.matrix.server.desktop.model.TimeModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;
import ua.softgroup.matrix.server.service.ClientSettingsService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class ServerSocketRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketRunner.class);

    private final MatrixServerApi matrixServerApi;
    private final ClientSettingsService clientSettingsService;
    private final Environment environment;
    private final LoadDefaultConfig defaultConfig;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    @Autowired
    public ServerSocketRunner(MatrixServerApi matrixServerApi, ClientSettingsService clientSettingsService, Environment environment, LoadDefaultConfig defaultConfig) {
        this.matrixServerApi = matrixServerApi;
        this.clientSettingsService = clientSettingsService;
        this.environment = environment;
        this.defaultConfig = defaultConfig;
    }

    @Override
    public void run(String... args) throws IOException {
        createServerSocket();
        processClientSettings();

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

                try {
                    processClientInput(command);
                } catch (Exception e) {
                    LOG.error("Error1", e);
                }
            }
        }
    }

    private void processClientSettings(){
        if(clientSettingsService.getAll().isEmpty()){
            clientSettingsService.save(defaultConfig.getClientSettings());
        }
    }

    private void createServerSocket() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(environment.getRequiredProperty("socket.server.port")));
    }

    private void acceptClientSocket() throws IOException {
        clientSocket = serverSocket.accept();
        LOG.info("Accepted client {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
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

    private ServerCommands readServerCommand() {
        try {
            return (ServerCommands) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            LOG.error("readServerCommand", e);
        }
        return ServerCommands.CLOSE;
    }

    private boolean clientRequestClose(ServerCommands command) throws IOException {
        if (ServerCommands.CLOSE == command) {
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
            String token = matrixServerApi.authenticate((UserPassword) objectInputStream.readObject());
            sendStringResponse(token);
        } else if (ServerCommands.GET_ALL_PROJECT == command) {
            TokenModel token = (TokenModel) objectInputStream.readObject();
            sendAllObjectsToClient(matrixServerApi.getUserActiveProjects(token));
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
            boolean syncFlag = matrixServerApi.sync((SynchronizedModel) objectInputStream.readObject());
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
        } else if (ServerCommands.ACTIVE_WINDOWS_LOG == command) {
            matrixServerApi.saveActiveWindowsLog((ActiveWindowsModel) objectInputStream.readObject());
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

    private void sendConstantStatus(String status) throws IOException {
        dataOutputStream.writeUTF(status);
        dataOutputStream.flush();
    }
}
