package ua.softgroup.matrix.server;

import org.jasypt.util.password.StrongPasswordEncryptor;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.model.ReportModel;
import ua.softgroup.matrix.server.model.UserPassword;

import java.io.*;
import java.net.Socket;

public class SimpleClient {

    public static void main(String args[]) {
        try {
            Socket socket = new Socket("localhost", 6666);

            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            InputStream is = socket.getInputStream();
//            ObjectInputStream objectInputStream = new ObjectInputStream(is);


            oos.writeObject(ServerCommands.AUTHENTICATE);
            UserPassword auth = new UserPassword("", "");
            auth.setUsername("ivan");
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            String encryptedPassword = passwordEncryptor.encryptPassword("123456");
            auth.setPassword(encryptedPassword);
            oos.writeObject(auth);

            DataInputStream in = new DataInputStream(is);
            String token = in.readUTF();
            System.out.println(token);

//            oos.writeObject(ServerCommands.GET_REPORT);
//            ReportModel reportRequest = new ReportModel(token, "", "");
//            reportRequest.setId(1488);
//            oos.writeObject(reportRequest);
//            ReportModel report = (ReportModel) objectInputStream.readObject();
//            System.out.println("title: " + report.getTitle());
//            System.out.println("desc: " + report.getDiscription());

//            oos.writeObject(ServerCommands.SAVE_REPORT);
//            ReportModel reportModel = new ReportModel(token, "title", "description");
//            oos.writeObject(reportModel);
//
//            ReportModel report = (ReportModel) objectInputStream.readObject();
//
//            String token = in.readUTF();
//            System.out.println(token);

            oos.writeObject(ServerCommands.CLOSE);

            oos.close();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
