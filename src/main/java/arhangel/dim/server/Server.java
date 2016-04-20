package arhangel.dim.server;

import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.PgMessageStore;
import arhangel.dim.core.store.PgUserStore;
import arhangel.dim.core.store.UserStore;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;

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
        }

    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }

}
