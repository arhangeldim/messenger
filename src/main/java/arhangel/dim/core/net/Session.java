package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.MessagesHandler;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler {

    private MessagesHandler messagesHandler;

    static Logger log = LoggerFactory.getLogger(Session.class);

    public Session(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.messagesHandler = new MessagesHandler(server);
    }

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

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        // TODO: Отправить клиенту сообщение
        out.write(new StringProtocol().encode(msg));
        out.flush(); // заставляем поток закончить передачу данных.
    }

    @Override
    public void onMessage(Message msg) {
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
        log.info("message received: ", msg);

        try {
            switch (msg.getType()) {
                case MSG_TEXT:
                    try {
                        out.write(new StringProtocol().encode(msg));
                        out.flush(); // заставляем поток закончить передачу данных.
                    } catch (IOException | ProtocolException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_LOGIN:
                    messagesHandler.executeLoginMessage(this, (LoginMessage) msg);
                    break;
                case MSG_CHAT_LIST:
                    messagesHandler.executeChatListMessage(this, (ChatListMessage) msg);
                    break;
                case MSG_CHAT_CREATE:
                    messagesHandler.executeChatCreateMessage(this, (ChatCreateMessage) msg);
                    break;
                default:
                    log.error("unsupported message");
                    break;
            }
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
