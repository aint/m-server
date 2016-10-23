package ua.softgroup.matrix.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.api.MatrixServerApi;
import ua.softgroup.matrix.server.api.MatrixServerApiImpl;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.model.UserPassword;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServerRunner.class);

    private static final int SERVER_PORT = 6666;
    private static final MatrixServerApi matrixServerApi = new MatrixServerApiImpl();

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;


    public static void main(String args[]) throws Exception {
        SocketServerRunner socketServerRunner = new SocketServerRunner();

        socketServerRunner.createServerSocket();
        LOG.info("Waiting for a client...");

        while (true) {
            socketServerRunner.acceptClientSocket();
            LOG.info("Client connected");

            socketServerRunner.openObjectInputStream();
            socketServerRunner.openDataOutputStream();
            socketServerRunner.openDataInputStream();

            ServerCommands command;
            while (!socketServerRunner.clientRequestClose(command = socketServerRunner.readServerCommand())) {
                LOG.info("Client entered command {}", command.name());

                socketServerRunner.processClientInput(command);
            }
        }
    }

    private void createServerSocket() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
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
            sendAllObjectsToClient(matrixServerApi.getAllProjects(token));
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
            TokenModel token = (TokenModel) objectInputStream.readObject();
            matrixServerApi.startWork(token);
        } else if (ServerCommands.END_WORK == command) {
            TokenModel token = (TokenModel) objectInputStream.readObject();
            matrixServerApi.endWork(token);
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
