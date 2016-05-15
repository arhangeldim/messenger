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
import arhangel.dim.core.dbservice.dao.UsersDao;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.CommandExecutor;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImpl;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable, AutoCloseable {

    private User user;

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private Connection connection;
    private MessageStoreImpl messageStore;
    private UserStoreImpl userStore;
    private UsersDao usersDao;
    private Protocol protocol;

    private CommandExecutor commandExecutor;

    private static Logger log = LoggerFactory.getLogger(Session.class);

    public Session(Socket socket, Protocol protocol) throws IOException, SQLException, ClassNotFoundException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        this.protocol = protocol;

        this.usersDao = new UsersDao();
        usersDao.init();

        this.messageStore = new MessageStoreImpl(usersDao);
        this.userStore = new UserStoreImpl(usersDao);
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        log.info("Handling message: {}", msg);
        try {
            commandExecutor.handleMessage(msg, this);
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            in.close();
            out.close();
            connection.close();
            Thread.currentThread().interrupt();
            log.info("Session closed");
        } catch (IOException | SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                if (read > 0) {
                    Message message = protocol.decode(Arrays.copyOf(buf, read));
                    onMessage(message);
                } else {
                    if (read == -1) {
                        close();
                    }
                }
            } catch (ProtocolException | IOException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageStoreImpl getMessageStore() {
        return messageStore;
    }

    public void setMessageStore(MessageStoreImpl messageStore) {
        this.messageStore = messageStore;
    }

    public UserStoreImpl getUserStore() {
        return userStore;
    }

    public void setUserStore(UserStoreImpl userStore) {
        this.userStore = userStore;
    }

}
