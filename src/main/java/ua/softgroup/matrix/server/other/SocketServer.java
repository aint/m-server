package ua.softgroup.matrix.server.other;

import ua.softgroup.matrix.server.security.TokenAuthService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    private static final int SERVER_PORT = 6666;

    private TokenAuthService tokenAuthService = new TokenAuthService();

//    public static void main(String[] args) throws Throwable {
    public static void nonMain(String[] args) throws Throwable {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client accepted");
            new Thread(new SocketProcessor(socket)).start();
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private SocketProcessor(Socket socket) throws Throwable {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        }

        public void run() {
            try {
                readClientInput();
                writeResponse("test response");
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    socket.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.out.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\n" +
                    "Server: YarServer/2009-09-09\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\n" +
                    "Connection: close\n";
            String result = response + s;
            outputStream.write(result.getBytes());
            outputStream.flush();
        }

        private void readClientInput() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while(true) {
                String s = br.readLine();
//                tokenAuthService.authenticate()
                System.out.println(s);
                if(s == null || s.trim().length() == 0) {
                    break;
                }
            }
        }
    }

}
