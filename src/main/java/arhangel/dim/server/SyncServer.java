package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.session.NioSession;
import arhangel.dim.session.Session;
import arhangel.dim.core.store.DaoFactory;
import arhangel.dim.core.store.PostgresqlDaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Основной класс для сервера сообщений
 * DOESN'T WORK NOW!!!
 */
public class SyncServer {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    volatile List<Session> sessions = new ArrayList<>();

    private DaoFactory dbFactory;

    private ThreadPoolExecutor service;

    public void start() {
        service = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        try {
            dbFactory = new PostgresqlDaoFactory();
        } catch (ClassNotFoundException e) {
            log.error("Database connection problems", e);
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("SyncServer started");

            while (true) {
                Socket socket = serverSocket.accept();
                log.info("New User connected.");

                if (service.getActiveCount() >= maxConnection) {
                    socket.close();
                    log.info("Limit of users already reached. User disconnected");
                    continue;
                }

                Runnable sessionExecutor = new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Session session = new Session(socket, SyncServer.this);
                            //todo replace with sycn session if it is necessary
                            Session session = new NioSession(null, null);

                            InputStream in = socket.getInputStream();

                            while (!Thread.currentThread().isInterrupted()) {
                                final byte[] buf = new byte[1024 * 64];
                                int read = in.read(buf);
                                if (read > 0) {
                                    log.info("Input message readed");
                                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                                    session.onMessage(msg);
                                }
                            }

                        } catch (IOException e) {
                            //something went wrong
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                    }
                };

                new Thread(sessionExecutor).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    static Logger log = LoggerFactory.getLogger(SyncServer.class);

    public static void main(String[] args) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncServer server = null;
                try {
                    Container context = new Container("server.xml");
                    server = (SyncServer) context.getByName("server");
                    log.info("SyncServer settings readed");
                } catch (InvalidConfigurationException | IllegalAccessException |
                        InvocationTargetException | InstantiationException | ClassNotFoundException e) {
                    log.error("Failed to create server");
                    return;
                }

                server.start();
            }
        }).start();


//        Protocol protocol = new StringProtocol();
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) service;
//
//        int port = 19000;
//
//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            log.info("SyncServer started.");
//
//            while (true) {
//                Socket socket = serverSocket.accept();
//                log.info("New User connected.");
//
//                InputStream in = socket.getInputStream();
//                OutputStream out = socket.getOutputStream();
//
////                if (poolExecutor.getActiveCount() >= maxC)
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        int port = 19000;
//
//        try {
//            ServerSocket ss = new ServerSocket(port);
//            System.out.println("Waiting for a client...");
//
//            Socket socket = ss.accept();
//
//            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
//            System.out.println();
//
//            InputStream sin = socket.getInputStream();
//            OutputStream sout = socket.getOutputStream();
//
//            String line = null;
//            while(true) {
//                byte[] data = new byte[1000];
//                sin.read(data);
//                Message msg = new StringProtocol().decode(data);
//                line = msg.toString();
//                System.out.println("The dumb client just sent me this line : " + line);
//                System.out.println("I'm sending it back...");
//                sout.write(new StringProtocol().encode(msg));
//                sout.flush(); // заставляем поток закончить передачу данных.
//                System.out.println("Waiting for the next line...");
//                System.out.println();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }


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

    public DaoFactory getDbFactory() {
        return dbFactory;
    }
}
