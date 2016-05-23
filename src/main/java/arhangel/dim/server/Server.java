package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.commands.ChatCreateCommand;
import arhangel.dim.core.commands.ChatHistoryCommand;
import arhangel.dim.core.commands.ChatListCommand;
import arhangel.dim.core.commands.GenericCommand;
import arhangel.dim.core.commands.InfoCommand;
import arhangel.dim.core.commands.LoginCommand;
import arhangel.dim.core.commands.TextCommand;
import arhangel.dim.core.commands.UserCreateCommand;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.net.StringProtocol;
import arhangel.dim.core.store.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;
    private static ServerSocket serverSocket;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static Logger log = LoggerFactory.getLogger(Server.class);

    private List<Session> sessions = new ArrayList<>();

    public Server() {
        port = 9000;
        protocol = new BinaryProtocol();
    }

    public static void main(String[] args) throws Exception {
        //Server server();
        // Пользуемся механизмом контейнера
        /*
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Invalid server configuration", e);
            return;
        }
*/

        log.info("Server created");

        Server server = new Server();

        Map<Type, GenericCommand> command = new HashMap<>();
        command.put(Type.MSG_CHAT_CREATE, new ChatCreateCommand());
        command.put(Type.MSG_USER_CREATE, new UserCreateCommand());
        command.put(Type.MSG_LOGIN, new LoginCommand());
        command.put(Type.MSG_CHAT_HIST, new ChatHistoryCommand());
        command.put(Type.MSG_CHAT_LIST, new ChatListCommand());
        command.put(Type.MSG_INFO, new InfoCommand());
        command.put(Type.MSG_TEXT, new TextCommand(server));

        Interpreter interpreter = new Interpreter(command);
        DataBase db = new DataBase();

        //Server server = new Server();

        try {
            serverSocket = new ServerSocket(server.getPort());

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                log.info("New session");
                Session session = new Session(clientSocket, server, interpreter);
                server.getSessions().add(session);
                //addSession(session);
                server.threadPool.execute(session);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            log.error("Cannot start new session", e);
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }


    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        threadPool.shutdown();
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

    public List<Session> getSessions() {
        return this.sessions;
    }
    public void addSession(Session session) {
        this.sessions.add(session);
    }

}
