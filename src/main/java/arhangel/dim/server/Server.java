package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.*;
import arhangel.dim.lections.socket.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private String dbLoc;
    private String dbLogin;
    private String dbPassword;
    private UserStore userStore;
    private MessageStore messageStore;
    private static Logger log = LoggerFactory.getLogger(Server.class);

    public Server() {
        port = 19000;
        protocol = new BinaryProtocol();
    }

    public static void main(String[] args) throws Exception {
        Server server;
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Invalid server configuration", e);
            return;
        }

        log.info("Server created");
        /* Create database if doesn't exist */
        Db dataBase = new Db(server.getDbLoc(), server.getDbLogin(), server.getDbPassword());
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(server.getPort());
            log.info("Server started, waiting for connection");
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                log.info("Accepted, " + clientSocket.getInetAddress());
                log.info("Starting new session");

                Session session = new Session(clientSocket, server);
                server.threadPool.execute(session);
            }

        } finally {
            IoUtil.closeQuietly(serverSocket);
        }
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public String getDbLoc() {
        return this.dbLoc;
    }

    public String getDbLogin() {
        return  this.dbLogin;
    }

    public String getDbPassword() {
        return  this.dbPassword;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }
}
