package arhangel.dim.server;

import arhangel.dim.core.command.LoginCommand;
import arhangel.dim.core.dbservice.dao.UsersDao;
import arhangel.dim.core.messages.CommandExecutor;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);

    public static final int DEFAULT_MAX_CONNECT = 16;

    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private ServerSocket serverSocket;
    private Set<Session> sessions = new HashSet<>();
    private CommandExecutor executor = new CommandExecutor();

    private UsersDao usersDao = new UsersDao();

    private MessageStore messageStore = new MessageStoreImpl(usersDao);
    private UserStore userStore = new UserStoreImpl(usersDao);

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);

        executor.addCommand(Type.MSG_LOGIN, new LoginCommand());
    }

    public void start() {
        threadPool.submit(listenForClients);
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        threadPool.shutdown();
    }

    // Сервер слушает клиентов. Каждому новому клиенту соответствует своя сессия, которая работает в новом потоке.
    private Runnable listenForClients = new Runnable() {
        @Override
        public void run() {
            try {
                while (!serverSocket.isClosed()) {
                    Socket newClientSocket = null;
                    try {
                        log.info("Waiting for connect...");
                        newClientSocket = serverSocket.accept();
                        log.info("Connected: " + newClientSocket.getInetAddress());
                    } catch (SocketException e) {
                        log.error(e.getMessage());
                    }
                    Session session = new Session(newClientSocket, protocol);
                    sessions.add(session);
                    threadPool.submit(session);
                }
            } catch (IOException | SQLException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
            if (!serverSocket.isClosed()) {
                threadPool.submit(listenForClients);
            }
        }
    };

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getIProtocol() {
        return protocol;
    }

    public void setIProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void  setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public void setUsersDao(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);
        server.start();
    }


}