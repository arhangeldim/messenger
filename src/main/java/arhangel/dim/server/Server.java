package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
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


    public int getMaxConnection() {
        return maxConnection;
    }

    public int getPort() {
        return port;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void stop() {
        threadPool.shutdown();
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
        out.flush();
    }


    public static void main(String[] args) {
        Server server = null;
        try {
//            Container context = new Container("server.xml");
//            server = (Server) context.getByName("server");
            server = new Server();
            Protocol protocol = new BinaryProtocol();
            server.setProtocol(protocol);
            server.setPort(19000);
            ServerSocket serverSocket = new ServerSocket(server.getPort());

            System.out.println("Started, waiting for connection");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();

                System.out.println("Accepted. " + clientSocket.getInetAddress());
                Session session = new Session(clientSocket, server);
                server.getSessionList().add(session);
                server.threadPool.execute(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
