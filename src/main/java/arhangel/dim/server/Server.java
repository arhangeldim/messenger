package arhangel.dim.server;

import arhangel.dim.container.Context;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import arhangel.dim.core.net.Session;
import arhangel.dim.lections.exception.ExceptionDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Основной класс для сервера сообщений
 */

public class Server {

    static Logger log = LoggerFactory.getLogger(Server.class);
    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    private ServerSocket socket;
    private ExecutorService service;

    //FIXME: concurrent
    private List<Session> sessions;

    public List<Session> getSessions() {
        return sessions;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public void init() throws IOException {
        socket = new ServerSocket(port);
        service = Executors.newFixedThreadPool(maxConnection);
        sessions = new ArrayList<>();
    }

    public void run() throws IOException {
        while (true) {
            Socket clientSocket = socket.accept();
            log.info("Accepted " + clientSocket.getInetAddress());

            service.submit(() -> {
                Session session = new Session(this);
                session.setUser(null);
                session.setSocket(clientSocket);
                try {
                    session.setIn(clientSocket.getInputStream());
                    session.setOut(clientSocket.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sessions.add(session);
                byte[] buf = new byte[256 * 64];
                while (true) {
                    /*int size = */session.getIn().read(buf);
                    //FIXME: check for closed socket and remove session from session list
                    session.onMessage(protocol.decode(buf));
                }
            });
        }
    }

    public static void main(String[] args) {

        Server server = null;
        // Пользуемся механизмом контейнера
        try {
            Context context = new Context("server.xml");
            server = (Server) context.getBeanByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server: configuration error", e);
            return;
        }
        try {

            server.init();
            server.run();
            /*
            client.initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }
            }*/
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
