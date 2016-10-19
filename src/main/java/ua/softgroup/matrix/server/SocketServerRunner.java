package ua.softgroup.matrix.server;

import ua.softgroup.matrix.server.api.MatrixServerApi;
import ua.softgroup.matrix.server.api.MatrixServerApiImpl;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.request.Authentication;
import ua.softgroup.matrix.server.request.Report;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerRunner {

    private static final int SERVER_PORT = 6666;
    private static final MatrixServerApi matrixServerApi = new MatrixServerApiImpl();

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;


    public static void main(String args[]) throws Exception {
        SocketServerRunner socketServerRunner = new SocketServerRunner();

        socketServerRunner.createServerSocket();
        System.out.println("Waiting for a client...\n");

        while (true) {
            socketServerRunner.acceptClientSocket();
            System.out.println("Client connected \n");

            socketServerRunner.openObjectInputStream();

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
        objectInputStream.close();
        clientSocket.close();
    }

    private void processClientInput(ServerCommands command) throws IOException, ClassNotFoundException {
        if (ServerCommands.AUTHENTICATE == command) {
            Authentication auth = (Authentication) objectInputStream.readObject();
            matrixServerApi.authenticate(auth.getLogin() + auth.getPassword());
        } else if (ServerCommands.GET_ALL_PROJECT == command) {
            matrixServerApi.getAllProjects();
        } else if (ServerCommands.SET_CURRENT_PROJECT == command) {
            matrixServerApi.setCurrentProject(0L);
        } else if (ServerCommands.GET_REPORT == command) {
            matrixServerApi.getReport(0L);
        } else if (ServerCommands.SAVE_REPORT == command) {
            Report report = (Report) objectInputStream.readObject();
            matrixServerApi.saveReport(report.getText(), report.getDate());
        } else if (ServerCommands.CLOSE == command) {
            closeClientSocket();
        } else {
            System.out.println("No such command");
        }
    }



}
