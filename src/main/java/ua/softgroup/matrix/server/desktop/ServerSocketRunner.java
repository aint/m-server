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
            new Thread(new SocketClientRunnable(serverSocket.accept())).start();
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

    private class SocketClientRunnable implements Runnable {

        private ObjectInputStream objectInputStream;
        private DataOutputStream dataOutputStream;
        private DataInputStream dataInputStream;

        private final Socket clientSocket;

        public SocketClientRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
            LOG.info("Client connected {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        }

        @Override
        public void run() {
            try {
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                dataInputStream = new DataInputStream(clientSocket.getInputStream());

                ServerCommands command;
                while (!clientRequestClose(command = readServerCommand())) {
                    processClientInput(command);
                }
            } catch (Exception e) {
                LOG.error("Error", e);
            }
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
            dataInputStream.close();
            objectInputStream.close();
            clientSocket.close();
        }

        private void sendObject(Object object) throws IOException {
            //TODO create OOS only once
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(object);
            out.flush();
        }

        private void sendString(String string) throws IOException {
            dataOutputStream.writeUTF(string);
            dataOutputStream.flush();
        }

        private Object readObject() throws IOException, ClassNotFoundException {
            return objectInputStream.readObject();
        }

        private void processClientInput(ServerCommands command) throws IOException, ClassNotFoundException {
            LOG.info("Client entered command {}", command.name());
            switch (command) {
                case AUTHENTICATE: {
                    String token = matrixServerApi.authenticate((UserPassword) readObject());
                    sendString(token);
                    break;
                }
                case GET_ALL_PROJECT: {
                    TokenModel token = (TokenModel) readObject();
                    sendObject(matrixServerApi.getUserActiveProjects(token));
                    break;
                }
                case SAVE_REPORT: {
                    ReportModel report = (ReportModel) readObject();
                    sendString(matrixServerApi.saveReport(report).name());
                    break;
                }
                case SAVE_SCREENSHOT: {
                    ScreenshotModel file = (ScreenshotModel) readObject();
                    matrixServerApi.saveScreenshot(file);
                    break;
                }
                case GET_REPORTS_BY_PROJECT_ID: {
                    TokenModel token = (TokenModel) readObject();
                    long id = dataInputStream.readLong();
                    sendObject(matrixServerApi.getAllReportsByProjectId(token, id));
                    break;
                }
                case START_WORK: {
                    TimeModel timeModel = (TimeModel) readObject();
                    matrixServerApi.startWork(timeModel);
                    break;
                }
                case END_WORK: {
                    TimeModel token = (TimeModel) readObject();
                    matrixServerApi.endWork(token);
                    break;
                }
                case START_DOWNTIME: {
                    TimeModel timeModel = (TimeModel) readObject();
                    matrixServerApi.startDowntime(timeModel);
                    break;
                }
                case STOP_DOWNTIME: {
                    TimeModel token = (TimeModel) readObject();
                    matrixServerApi.endDowntime(token);
                    break;
                }
                case GET_TODAY_TIME: {
                    TimeModel workTime = matrixServerApi.getTodayWorkTime((TimeModel) readObject());
                    sendObject(workTime);
                    break;
                }
                case GET_TOTAL_TIME: {
                    TimeModel workTime = matrixServerApi.getTotalWorkTime((TimeModel) readObject());
                    sendObject(workTime);
                    break;
                }
                case UPDATE_SETTING: {
                    sendObject(matrixServerApi.getClientSettings());
                    break;
                }
                case KEYBOARD_LOG: {
                    WriteKeyboard keyboard = (WriteKeyboard) readObject();
                    matrixServerApi.saveKeyboardLog(keyboard);
                    break;
                }
                case ACTIVE_WINDOWS_LOG: {
                    ActiveWindowsModel activeWindows = (ActiveWindowsModel) readObject();
                    matrixServerApi.saveActiveWindowsLog(activeWindows);
                    break;
                }
                case CLOSE: {
                    closeClientSocket();
                    break;
                }
                default:
                    LOG.warn("No such command {}", command);
            }
        }
    }

}
