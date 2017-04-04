package ua.softgroup.matrix.server.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.api.ServerCommands;
import ua.softgroup.matrix.api.model.datamodels.AuthModel;
import ua.softgroup.matrix.api.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.datamodels.SynchronizationModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseStatus;
import ua.softgroup.matrix.server.desktop.api.MatrixServerApi;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

@Component
public class ServerSocketRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketRunner.class);

    private static final char[] PRIVATE_PASSPHRASE = "make_matrix_great_again".toCharArray();
    private static final char[] PUBLIC_PASSPHRASE = "public".toCharArray();
    private static final String PUBLIC_KEY_FILE = "keys/client.public";
    private static final String PRIVATE_KEY_FILE = "keys/server.private";
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String ALGORITHM = "SunX509";

    private KeyStore clientKeyStore;
    private KeyStore serverKeyStore;

    private final MatrixServerApi matrixServerApi;
    private final Environment environment;

    @Autowired
    public ServerSocketRunner(MatrixServerApi matrixServerApi, Environment environment) {
        this.matrixServerApi = matrixServerApi;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws IOException, GeneralSecurityException {
        int serverPort = Integer.parseInt(environment.getRequiredProperty("socket.server.port"));
        SSLServerSocket serverSocket = (SSLServerSocket) setupSSLContext().getServerSocketFactory().createServerSocket(serverPort);
        serverSocket.setNeedClientAuth(true);

        LOG.info("Waiting for a client...");

        while (true) {
            new Thread(new SocketClientRunnable(serverSocket.accept())).start();
        }
    }

    private void setupClientKeyStore() throws GeneralSecurityException, IOException {
        clientKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        clientKeyStore.load(new ClassPathResource(PUBLIC_KEY_FILE).getInputStream(), PUBLIC_PASSPHRASE);
    }
    private void setupServerKeystore() throws GeneralSecurityException, IOException {
        serverKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        serverKeyStore.load(new ClassPathResource(PRIVATE_KEY_FILE).getInputStream(), PRIVATE_PASSPHRASE);
    }
    private SSLContext setupSSLContext() throws GeneralSecurityException, IOException {
        setupClientKeyStore();
        setupServerKeystore();

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(ALGORITHM);
        tmf.init(clientKeyStore);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(ALGORITHM);
        kmf.init(serverKeyStore, PRIVATE_PASSPHRASE);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
        return sslContext;
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
                case SYNCHRONIZE: {
                    RequestModel<SynchronizationModel> requestModel = (RequestModel<SynchronizationModel>) readObject();
                    sendObject(matrixServerApi.syncCheckpoints(requestModel));
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
