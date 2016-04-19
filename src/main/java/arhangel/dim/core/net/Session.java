package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImplementation;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImplementation;
import arhangel.dim.server.Interpreter;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */

public class Session implements ConnectionHandler, Runnable, AutoCloseable {

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    private User user;

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private Connection connection;
    private MessageStore messageStore;
    private UserStore userStore;
    private Logger log = LoggerFactory.getLogger(Session.class);
    private Interpreter interpreter;
    private Server server;
    private Protocol protocol;

    public Session(Socket socket, Server server, Interpreter interpreter) throws IOException,
            ClassNotFoundException, SQLException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.server = server;
        this.interpreter = interpreter;
        this.protocol = server.getProtocol();

        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/PotapovaSofia",
                "trackuser", "trackuser");
        this.messageStore = new MessageStoreImplementation(connection);
        this.userStore = new UserStoreImplementation(connection);
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        log.info("Sending message: {}", msg);
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        log.info("Handling message: {}", msg);
        try {
            interpreter.handleMessage(msg, this);
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            log.info("Session closed");
            in.close();
            out.close();
            connection.close();
            Thread.currentThread().interrupt();
        } catch (IOException | SQLException e) {
            log.error("Cannot close resources");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.info("Session running");
        while (!getSocket().isClosed()) {
            byte[] buf = new byte[1024 * 64];
            int readBytes = 0;
            try {
                readBytes = in.read(buf);
                if (readBytes > 0) {
                    Message msg = null;
                    msg = protocol.decode(buf);
                    onMessage(msg);
                }
            } catch (IOException | ProtocolException e) {
                log.error("Server error", e);
                e.printStackTrace();
            }
        }
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
