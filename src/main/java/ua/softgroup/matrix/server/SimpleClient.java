package ua.softgroup.matrix.server;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.model.UserPassword;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleClient {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleClient.class);

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
            String encryptedPassword = passwordEncryptor.encryptPassword("111111");
            auth.setPassword(encryptedPassword);
            oos.writeObject(auth);

            DataInputStream in = new DataInputStream(is);
            String token = in.readUTF();
            LOG.debug(token);

//            oos.writeObject(ServerCommands.GET_REPORT);
//            ReportModel reportRequest = new ReportModel(token, "", "");
//            reportRequest.setId(1488);
//            oos.writeObject(reportRequest);
//            ReportModel report = (ReportModel) objectInputStream.readObject();
//            LOG.debug("title: " + report.getTitle());
//            LOG.debug("desc: " + report.getDiscription());

//            oos.writeObject(ServerCommands.SAVE_REPORT);
//            ReportModel reportModel = new ReportModel(token, "title", "description");
//            oos.writeObject(reportModel);
//
//            ReportModel report = (ReportModel) objectInputStream.readObject();
//
//            String token = in.readUTF();
//            LOG.debug(token);

            oos.writeObject(ServerCommands.CLOSE);

            oos.close();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
