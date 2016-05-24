package arhangel.dim.core.net;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateCommand;
import arhangel.dim.core.messages.ChatHistoryCommand;
import arhangel.dim.core.messages.ChatListCommand;
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
    static Logger log = LoggerFactory.getLogger(Session.class);

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

    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
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
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
        switch (msg.getType()) {
            case MSG_REGISTER:
                RegisterCommand registerCommand = new RegisterCommand();
                try {
                    registerCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_LOGIN:
                LoginCommand loginCommand = new LoginCommand();
                try {
                    loginCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_TEXT:
                msg.setSenderId(user.getId());
                TextCommand textCommand = new TextCommand();
                try {
                    textCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_INFO:
                InfoCommand infoCommand = new InfoCommand();
                try {
                    infoCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_CHAT_CREATE:
                ChatCreateCommand chatCreateCommand = new ChatCreateCommand();
                try {
                    chatCreateCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_CHAT_LIST:
                ChatListCommand chatListCommand = new ChatListCommand();
                try {
                    chatListCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_CHAT_HIST:
                ChatHistoryCommand chatHistoryCommand = new ChatHistoryCommand();
                try {
                    chatHistoryCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                return;
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
