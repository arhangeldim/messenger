package arhangel.dim.server;

import arhangel.dim.client.Client;
import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    static Logger log = LoggerFactory.getLogger(Client.class);

    private InputStream in;
    private OutputStream out;
    private Thread serverSocketThread;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void initSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocketThread = new Thread(() -> {
            Socket fromclient;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    log.info("Waiting for a client...");
                    fromclient = serverSocket.accept();
                    log.info("Client connected");
                    in = fromclient.getInputStream();
                    out = fromclient.getOutputStream();
                    Session clientSession = new Session(in, out, protocol);
                    clientSession.run();
                } catch (IOException e) {
                    log.info("Can't accept");
                    Thread.currentThread().interrupt();
                }
            }
        });

        serverSocketThread.start();
    }
    public static void main(String[] args) throws Exception {

        Server server = null;

        // Пользуемся механизмом контейнера
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server", e);
            return;
        }
        try {
            server.initSocket();
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (server != null) {
                server.stop();
            }
        }

    }
    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

}
