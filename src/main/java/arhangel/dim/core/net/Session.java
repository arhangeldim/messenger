package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Arrays;

import arhangel.dim.container.Property;
import arhangel.dim.core.User;
import arhangel.dim.core.commands.CommandException;
import arhangel.dim.core.commands.CommandExecutor;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImpl;
import arhangel.dim.server.Server;
import org.slf4j.Logger;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    private User user;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Protocol protocol;
    private Logger log = Server.log;
    private SimpleDataBaseConnectionPool connectionPool;
    private Connection connection;
    private CommandExecutor commandExecutor;
    private UserStore userStore;
    private MessageStore messageStore;

    public Session(Socket socket, Protocol protocol, SimpleDataBaseConnectionPool connectionPool,
                   CommandExecutor commandExecutor) throws IOException, ConnectionPoolException {
        this.socket = socket;
        this.protocol = protocol;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.connectionPool = connectionPool;
        this.commandExecutor = commandExecutor;
        synchronized (connectionPool) {
            connection = connectionPool.getConnection();
        }
        this.messageStore = new MessageStoreImpl(connection);
        this.userStore = new UserStoreImpl(connection);
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        if (user != null) {
            msg.setSenderId(user.getId());
        }
        try {
            Message result = commandExecutor.processCommand(msg, this);
            send(result);
        } catch (CommandException | ProtocolException | IOException e) {
            log.error("Command don't work", e);
        }
    }

    @Override
    public void close() {
        try {
            try {
                send(new StatusMessage("Connection lost"));
            } catch (ProtocolException e) {
                log.error("connection lost", e);
            }
            in.close();
            out.close();
            socket.close();
            synchronized (connectionPool) {
                connectionPool.putConnection(connection);
            }
            log.info("Session closed");
        } catch (IOException e) {
            log.error("Cannot close session", e);
        }
    }

    @Override
    public void run() {
        log.info("Session running");

        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                if (read > 0) {
                    log.info("Incoming message");
                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                    onMessage(msg);
                } else {
                    if (read == -1) {
                        close();
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.error("Server error", e);
                close();
                Thread.currentThread().interrupt();
            }
        }
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
