package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.PgMessageStore;
import arhangel.dim.core.store.PgUserStore;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    public static Logger getLog() {
        return log;
    }

    private static Logger log = LoggerFactory.getLogger(Server.class);

    // Засетить из конфига
    private int port;

    public boolean isFinished() {
        return finished;
    }

    private boolean finished = false;



    private MessageStore messageStore;
    private UserStore userStore;

    private int maxConnection = DEFAULT_MAX_CONNECT;

    private ExecutorService service;

    private ConcurrentHashMap<Long, Session> activeUsers;

    public ConcurrentHashMap<Long, Session> getActiveUsers() {
        return activeUsers;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(Protocol protocol) {

        this.protocol = protocol;
    }

    private Protocol protocol;

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void start() throws IOException {
        activeUsers = new ConcurrentHashMap<>();
        service = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECT);
        port = 19000;
        protocol = new BinaryProtocol();
        messageStore = new PgMessageStore();
        userStore = new PgUserStore();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Started");

        while (true) {
            Socket socket = serverSocket.accept();
            Session session = new Session(socket, this);
            service.submit(session);
            try {
                sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop();
        }

    }

    public void stop() {
        finished = true;
        try {
            service.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = null;
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }
        server.start();
    }

}
