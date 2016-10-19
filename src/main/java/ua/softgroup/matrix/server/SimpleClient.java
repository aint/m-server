package ua.softgroup.matrix.server;

import ua.softgroup.matrix.server.api.ServerCommands;
import ua.softgroup.matrix.server.request.Authentication;
import ua.softgroup.matrix.server.request.Report;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;

public class SimpleClient {

    public static void main(String args[]) {
        try {
            Socket socket = new Socket("localhost", 6666);

            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);


            oos.writeObject(ServerCommands.AUTHENTICATE);
            Authentication auth = new Authentication("vasia", "123456");
            oos.writeObject(auth);

            oos.writeObject(ServerCommands.SAVE_REPORT);
            Report report = new Report("text text text text ", LocalDate.now());
            oos.writeObject(report);

            oos.writeObject(ServerCommands.CLOSE);

            oos.close();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
