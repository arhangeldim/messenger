package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.server.commands.ChatCreateCommand;
import arhangel.dim.server.commands.ChatHistoryCommand;
import arhangel.dim.server.commands.ChatListCommand;
import arhangel.dim.server.commands.InfoCommand;
import arhangel.dim.server.commands.LoginCommand;
import arhangel.dim.server.commands.TextCommand;
import arhangel.dim.server.commands.UserCreateCommand;
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

    public static final int DEFAULT_MAX_CONNECT = 16;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        Server server;
        // Пользуемся механизмом контейнера
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server", e);
            return;
        }

        log.info("Server created");

        CommandExecutor commandExecutor = new CommandExecutor()
                .addCommand(Type.MSG_CHAT_CREATE, new ChatCreateCommand())
                .addCommand(Type.MSG_CHAT_HIST, new ChatHistoryCommand())
                .addCommand(Type.MSG_CHAT_LIST, new ChatListCommand())
                .addCommand(Type.MSG_INFO, new InfoCommand())
                .addCommand(Type.MSG_LOGIN, new LoginCommand())
                .addCommand(Type.MSG_TEXT, new TextCommand())
                .addCommand(Type.MSG_USER_CREATE, new UserCreateCommand());

        ExecutorService service = Executors.newFixedThreadPool(server.getMaxConnection());

        try {
            server.serverSocket = new ServerSocket(server.getPort());
            //TODO Thread pool
            while (true) {
                Socket socket = server.serverSocket.accept();
                log.info("New session");
                Session session = new Session(socket, server.getProtocol(), commandExecutor);
                service.submit(session);
                log.info("Session submitted");
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            log.error("Cannot start new session", e);
            e.printStackTrace();
        }

    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
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

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }
}
