package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.container.Main;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    static Logger log = LoggerFactory.getLogger(Server.class);

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    private InputStream in;
    private OutputStream out;

    Connection connection;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    List<Session> sessionList = new ArrayList<>();


    public int getMaxConnection() { return maxConnection;}
    public int getPort() {
        return port;
    }
    public Protocol getProtocol() { return protocol;}

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public Message recieve() throws IOException, ProtocolException {
        byte[] buf = new byte[1024 * 500];
        int readBytes = in.read(buf);

        return protocol.decode(buf);
    }

    public List<Session> getSessionList() {
        return sessionList;
    }

    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
        out.close();
    }


    public static void main(String[] args) {

        try {
            Container context = new Container("server.xml");
            Server server = (Server) context.getByName("server");
            ServerSocket serverSocket = new ServerSocket(server.getPort());

            System.out.println("Started, waiting for connection");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();

                System.out.println("Accepted. " + clientSocket.getInetAddress());
                Session session = new Session(clientSocket, server);
                server.getSessionList().add(session);


               // server.threadPool.execute(new Session(clientSocket, server.getProtocol()));
                while (true) {
                    byte[] buf = new byte[1024 * 500];
                    int readBytes = 0;
                    try {
                        readBytes = session.getIn().read(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (readBytes > 0) {
                        Message msg = null;
                        try {
                            msg = server.getProtocol().decode(buf);
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                        session.onMessage(msg);
                    }
                }
            }
            //server.threadPool.shutdown();

            } catch (Exception e) {
            e.printStackTrace();
            } finally {

            }

    }
}
