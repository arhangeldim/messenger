package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.CycleReferenceException;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.commands.CommandExecutor;
import arhangel.dim.core.net.ConnectionPoolException;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.net.SimpleDataBaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static Logger log = LoggerFactory.getLogger(Server.class);
    public static final int DEFAULT_MAX_CONNECT = 3;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public static void main(String... args) {
        Server server = null;
        CommandExecutor commandExecutor = new CommandExecutor();
        try {
            Container container = new Container("server.xml");
            server = (Server) container.getByName("server");
        } catch (InvalidConfigurationException | CycleReferenceException e) {
            log.error("Failed to create server", e);
            return;
        }
        log.info("Server started");

        try {
            SimpleDataBaseConnectionPool connectionPool = new SimpleDataBaseConnectionPool(
                    server.getMaxConnection(),
                    "jdbc:postgresql://178.62.140.149:5432/NobodyLikesZergs",
                    "trackuser", "trackuser"); //TODO use container
            ExecutorService executorService = Executors.newFixedThreadPool(server.getMaxConnection());
            ServerSocket serverSocket = null;
            serverSocket = new ServerSocket(server.getPort());
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    Session session = new Session(socket, server.getProtocol(), connectionPool, commandExecutor);
                    executorService.submit(session);
                    log.info("Session started");
                } catch (ConnectionPoolException e) {
                    log.error("Connection pool is empty");
                }
            }

        } catch (IOException e) {
            log.error("Session failed", e);
        } catch (SQLException e) {
            log.error("Failed to create connection pool");
            return;
        }
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
