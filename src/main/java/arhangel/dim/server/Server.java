package arhangel.dim.server;

import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.SMessageStore;
import arhangel.dim.core.store.SUserStore;

import java.io.IOException;
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
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    SMessageStore messageStore;
    SUserStore userStore;
    private ExecutorService service;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setMessageStore(SMessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public SMessageStore getMessageStore() {
        return messageStore;
    }

    public void setUserStore(SUserStore userStore) {
        this.userStore = userStore;
    }

    public SUserStore getUserStore() {
        return userStore;
    }

    private ConcurrentHashMap<Long, Session> activeUsers;

    public ConcurrentHashMap<Long, Session> getActiveUsers() {
        return activeUsers;
    }


    public void start() throws IOException {
        activeUsers = new ConcurrentHashMap<>();
        service = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECT);
        port = 19000;
        protocol = new BinaryProtocol();
        messageStore = new SMessageStore();
        userStore = new SUserStore();

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
