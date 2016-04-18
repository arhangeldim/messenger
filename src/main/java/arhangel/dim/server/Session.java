package arhangel.dim.server;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImplementation;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    private User user;

    // сокет на клиента
    private Socket socket;

    private Connection connection;
    private MessageStore messageStore;
    private UserStore userStore;


    private Logger log = LoggerFactory.getLogger(Session.class);

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private Protocol protocol;

    private CommandExecutor commandExecutor;

    public Session(Socket socket, Protocol protocol, CommandExecutor commandExecutor) throws IOException, ClassNotFoundException, SQLException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.protocol = protocol;
        this.commandExecutor = commandExecutor;

        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/dmitryKonturov",
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
            commandExecutor.handleMessage(msg, this);
        } catch (CommandException e) {
            //TODO send message to user about server error
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            Thread.currentThread().interrupt();
            in.close();
            out.close();
            connection.close();
        } catch (IOException | SQLException e) {
            log.error("Cannot close resources");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.info("Session running");

        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                log.info("Incoming message");
                if (read > 0) {
                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                    onMessage(msg);
                }
            } catch (Exception e) {
                log.error("Server error", e);
                Thread.currentThread().interrupt();
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

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
