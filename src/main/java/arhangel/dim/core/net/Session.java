package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.commands.ComChatList;
import arhangel.dim.core.messages.commands.ComInfo;
import arhangel.dim.core.messages.commands.ComLogin;
import arhangel.dim.core.messages.commands.ComText;
import arhangel.dim.core.messages.commands.ComChatCreate;
import arhangel.dim.core.messages.commands.ComChatHist;
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
    private Server server;
    private Protocol protocol;
    private Logger log = LoggerFactory.getLogger(Session.class);
    private UserStore userStore;
    private MessageStore messageStore;

    public Session(Socket socket, Server server) throws IOException, SQLException, ClassNotFoundException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.server = server;
        this.protocol = server.getProtocol();

        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(server.getDbLoc(), server.getDbLogin(),
                server.getDbPassword());
        this.userStore = new UserStoreImpl(connection);
        this.messageStore = new MessageStoreImpl(connection);
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        log.info("Sending message: {}", msg);
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) throws IOException, ProtocolException {
        log.info("Process message: {}", msg.getType());
        try {
            switch (msg.getType()) {
                case MSG_LOGIN:
                    ComLogin.execute(this, msg);
                    break;
                case MSG_TEXT:
                    ComText.execute(this, msg);
                    break;
                case MSG_INFO:
                    ComInfo.execute(this, msg);
                    break;
                case MSG_CHAT_LIST:
                    ComChatList.execute(this, msg);
                    break;
                case MSG_CHAT_CREATE:
                    ComChatCreate.execute(this, msg);
                    break;
                case MSG_CHAT_HIST:
                    ComChatHist.execute(this, msg);
                    break;
                default:
                    log.info("Unknown command");
                    StatusMessage response = new StatusMessage();
                    send(response);
            }
        } catch (CommandException e) {
            log.error("Caught command exception");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        log.info("Session running");
        while (!socket.isClosed()) {
            byte[] buf = new byte[1024 * 64];
            try {
                int readBytes = in.read(buf);
                if (readBytes > 0) {
                    Message received = protocol.decode(buf);
                    onMessage(received);
                }

            } catch (IOException | ProtocolException e) {
                log.error("Server error occured: " + e.getCause().toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        try {
            log.info("Trying to close in/out channels and socket");
            in.close();
            out.close();
            connection.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Can't close in or out channels");
            e.printStackTrace();
        } catch (SQLException e) {
            log.error("Can't close connection with database");
            e.printStackTrace();
        }
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public void authUser(User user) {
        this.user = user;
    }
}
