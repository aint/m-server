package ua.softgroup.matrix.server;

import ua.softgroup.matrix.server.api.MatrixServerApi;
import ua.softgroup.matrix.server.api.MatrixServerApiImpl;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.ScreenshotModel;
import ua.softgroup.matrix.server.model.TokenModel;
import ua.softgroup.matrix.server.model.UserPassword;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Set;

public class SocketServerRunner {

    private static final int SERVER_PORT = 6666;
    private static final MatrixServerApi matrixServerApi = new MatrixServerApiImpl();

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private DataOutputStream dataOutputStream;


    public static void main(String args[]) throws Exception {
        SocketServerRunner socketServerRunner = new SocketServerRunner();

        socketServerRunner.createServerSocket();
        System.out.println("Waiting for a client...\n");

        while (true) {
            socketServerRunner.acceptClientSocket();
            System.out.println(LocalDateTime.now() + " Client connected \n");

            socketServerRunner.openObjectInputStream();
            socketServerRunner.openDataInputStream();

            ServerCommands command;
            while (!socketServerRunner.clientRequestClose(command = socketServerRunner.readServerCommand())) {
                System.out.println("Client enter command: " + command.name() + "\n");

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

    private void openDataInputStream() throws IOException {
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
            System.out.println("Client quit");
            System.out.println("-----------------------");
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
            System.out.println("TOKEN " + token);
            sendStringResponse(token);
        } else if (ServerCommands.GET_ALL_PROJECT == command) {
            matrixServerApi.getAllProjects();
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
            sendAllReportsToClient(matrixServerApi.getAllReports(token));
        } else if (ServerCommands.CLOSE == command) {
            closeClientSocket();
        } else {
            System.out.println("No such command");
        }
    }

    private void sendAllReportsToClient(Set<ReportModel> reports) throws IOException {
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
