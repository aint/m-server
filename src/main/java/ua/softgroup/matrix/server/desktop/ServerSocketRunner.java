package ua.softgroup.matrix.server.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.config.LoadDefaultConfig;
import ua.softgroup.matrix.server.desktop.api.MatrixServerApi;
import ua.softgroup.matrix.api.ServerCommands;
import ua.softgroup.matrix.api.model.datamodels.AuthModel;
import ua.softgroup.matrix.api.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.service.ClientSettingsService;

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
    public ServerSocketRunner(MatrixServerApi matrixServerApi, ClientSettingsService clientSettingsService,
                              Environment environment, LoadDefaultConfig defaultConfig) {
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

        private ObjectOutputStream objectOutputStream;
        private ObjectInputStream objectInputStream;

        private final Socket clientSocket;

        public SocketClientRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
            LOG.info("Client connected {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        }

        @Override
        public void run() {
            try {
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                ServerCommands command;
                while (!clientRequestClose(command = readServerCommand())) {
                    processClientInput(command);
                }
            } catch (Exception e) {
                LOG.error("Error", e);
                sendFailAndClose();
            }
        }

        private ServerCommands readServerCommand() throws IOException, ClassNotFoundException {
            return (ServerCommands) objectInputStream.readObject();
        }

        private boolean clientRequestClose(ServerCommands command) throws IOException {
            if (ServerCommands.CLOSE == command) {
                clientSocket.close();
                LOG.info("Client closed connection");
                LOG.info("-----------------------");
                return true;
            }
            return false;
        }

        private void sendFailAndClose() {
            LOG.warn("Send FAIL and close");
            try {
                sendObject(new ResponseModel<>(ResponseStatus.FAIL));
                clientSocket.close();
            } catch (IOException e) {
                LOG.warn("Shit happens", e);
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void processClientInput(ServerCommands command) throws IOException, ClassNotFoundException {
            LOG.info("Client entered command {}", command.name());
            switch (command) {
                case AUTHENTICATE: {
                    RequestModel<AuthModel> authRequest = (RequestModel<AuthModel>) readObject();
                    sendObject(matrixServerApi.authenticate(authRequest));
                    break;
                }
                case GET_REPORTS: {
                    RequestModel request = (RequestModel) readObject();
                    sendObject(matrixServerApi.getProjectReports(request));
                    break;
                }
                case SAVE_REPORT: {
                    RequestModel<ReportModel> reportRequest = (RequestModel<ReportModel>) readObject();
                    sendObject(matrixServerApi.saveReport(reportRequest));
                    break;
                }
                case START_WORK: {
                    RequestModel requestModel = (RequestModel) readObject();
                    sendObject(matrixServerApi.startWork(requestModel));
                    break;
                }
                case END_WORK: {
                    RequestModel requestModel = (RequestModel) readObject();
                    sendObject(matrixServerApi.endWork(requestModel));
                    break;
                }
                case CHECK_POINT: {
                    RequestModel<CheckPointModel> requestModel = (RequestModel<CheckPointModel>) readObject();
                    sendObject(matrixServerApi.processCheckpoint(requestModel));
                    break;
                }
                case CLOSE: {
                    clientSocket.close();
                    break;
                }
                default:
                    LOG.warn("No such command {}", command);
            }
        }

        private void sendObject(Object object) throws IOException {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        }

        private Object readObject() throws IOException, ClassNotFoundException {
            return objectInputStream.readObject();
        }
    }

}
