package arhangel.dim.core.net;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateCommand;
import arhangel.dim.core.messages.ChatHistoryCommand;
import arhangel.dim.core.messages.ChatListCommand;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.InfoCommand;
import arhangel.dim.core.messages.LoginCommand;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.RegisterCommand;
import arhangel.dim.core.messages.TextCommand;
import arhangel.dim.server.Server;
import arhangel.dim.server.WriteCompletionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler {
    private static Logger log = LoggerFactory.getLogger(Session.class);

    private Server server;
    private User user;
    private AsynchronousSocketChannel asynchronousSocketChannel;


    public Session(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean userAuthenticated() {
        return (user != null);
    }

    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        asynchronousSocketChannel.write(ByteBuffer.wrap(server.getProtocol().encode(msg)),
                this,
                new WriteCompletionHandler(server));
    }

    @Override
    public void onMessage(Message msg) {
        if (user != null) {
            msg.setSenderId(user.getId());
        }
        Command command;
        switch (msg.getType()) {
            case MSG_REGISTER:
                command = new RegisterCommand();
                break;
            case MSG_LOGIN:
                command = new LoginCommand();
                break;
            case MSG_TEXT:
                command = new TextCommand();
                break;
            case MSG_INFO:
                command = new InfoCommand();
                break;
            case MSG_CHAT_CREATE:
                command = new ChatCreateCommand();
                break;
            case MSG_CHAT_LIST:
                command = new ChatListCommand();
                break;
            case MSG_CHAT_HIST:
                command = new ChatHistoryCommand();
                break;
            default:
                return;
        }
        try {
            command.execute(this, msg);
        } catch (Exception e) {
            log.error("[onMessage] Failed to execute command", e);
        }
    }

    @Override
    public void close() {
        log.info("[close] Closing session with {}", user);
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            log.error("[close] Couldn't close socket channel, fuck it", e);
        }
    }
}
