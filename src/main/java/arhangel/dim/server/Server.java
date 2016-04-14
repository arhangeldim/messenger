package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
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

    public int getPort() {
        return port;
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public Message recieve() throws IOException, ProtocolException {
        byte[] buf = new byte[1024 * 500];
        int readBytes = in.read(buf);

        return protocol.decode(buf);
    }

    public void commandHandle(Message msg) {
        String type = msg.getType().toString();
        try {
            switch (type) {
                case "MSG_LOGIN":
                    LoginMessage loginMessage = (LoginMessage) msg;
                    UserDao userDao = new UserDao();

                    User founduser = userDao.getUser(loginMessage.getLogin(), loginMessage.getPassword());

                    if (founduser == null) {
                        founduser = new User(loginMessage.getLogin(), loginMessage.getPassword());
                        founduser = userDao.addUser(founduser);
                    }
                    InfoResultMessage result = new InfoResultMessage(founduser.getId(), founduser.getLogin(), founduser.getPassword(), null);
                    send(result);
                    break;
                default: throw new CommandException("Несуществующая команда сервера");
            }
        } catch (Exception e) {
            e.getMessage();
        }
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

            Socket socket = serverSocket.accept();

            System.out.println("Accepted. " + socket.getInetAddress());

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            while (true) {
                byte[] buf = new byte[1024 * 500];
                int readBytes = in.read(buf);
                if (readBytes != 0) {
                    Message msg = server.protocol.decode(buf);
                    server.commandHandle(msg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
