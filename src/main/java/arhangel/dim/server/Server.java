package arhangel.dim.server;

import arhangel.dim.client.Client;

import arhangel.dim.commandhandler.ChatCreateHandler;
import arhangel.dim.commandhandler.ChatHistHandler;
import arhangel.dim.commandhandler.ChatListHandler;
import arhangel.dim.commandhandler.InfoHandler;
import arhangel.dim.commandhandler.TextHandler;
import arhangel.dim.commandhandler.LoginHandler;
import arhangel.dim.commandhandler.RegistryHandler;
import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;
    public static HashMap<Type, Command> map = new HashMap<Type, Command>();
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

    public void listen() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket fromclient;
        while (true) {
            try {
                log.info("Waiting for a client...");
                fromclient = serverSocket.accept();
                log.info("Client connected");
                Session clientSession = new Session(fromclient, protocol);
                Thread thread = new Thread(clientSession);
                thread.start();
                log.info("Session closed");
                fromclient.close();
            } catch (IOException e) {
                log.info("Can't accept");
                break;
            }
        }

        log.info("Listener Stopped");
    }

    public static void setMap() {
        map.put(Type.MSG_CHAT_CREATE, new ChatCreateHandler());
        map.put(Type.MSG_CHAT_HIST, new ChatHistHandler());
        map.put(Type.MSG_CHAT_LIST, new ChatListHandler());
        map.put(Type.MSG_INFO, new InfoHandler());
        map.put(Type.MSG_REGISTER, new RegistryHandler());
        map.put(Type.MSG_TEXT, new TextHandler());
        map.put(Type.MSG_LOGIN, new LoginHandler());
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
        setMap();
        try {
            server.listen();
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
