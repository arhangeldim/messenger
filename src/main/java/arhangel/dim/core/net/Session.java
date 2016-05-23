package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.messages.commands.*;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.server.Server;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    private static HashMap<Type, Command> messageToCommand;

    static {
        messageToCommand = new HashMap<>();
        messageToCommand.put(Type.MSG_LOGIN, new LoginMessageCommand());
        messageToCommand.put(Type.MSG_TEXT, new TextMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_LIST, new ChatListMessageCommand());
    }

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    static final int MAX_MESSAGE_SIZE = 65536;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    private Server server;

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        if (user == null) {
            return;
        }
        out.write(server.getProtocol().encode(msg));
        out.flush();
        // TODO: Отправить клиенту сообщение
    }

    @Override
    public void onMessage(Message msg) {
        System.out.println(msg);
        try {
            messageToCommand.get(msg.getType()).execute(this, msg);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
    }

    @Override
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }

    public Session(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte [] binMessage = new byte[MAX_MESSAGE_SIZE];
        while (true) {
            try {
                in.read(binMessage);
                Message message = server.getProtocol().decode(binMessage);
                onMessage(message);
            } catch (IOException | ProtocolException e) {
                e.printStackTrace();
            }
        }
    }
}
