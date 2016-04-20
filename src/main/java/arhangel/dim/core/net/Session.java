package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.server.Server;

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
    static final int MAX_MESSAGE_SIZE = 65536;
    private User user;

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;
    private Server server;

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        if (user == null) {
            return;
        }
        // TODO: Отправить клиенту сообщение
    }

    @Override
    public void onMessage(Message msg) {
        System.out.println(msg);
        switch (msg.getType()) {
            case MSG_LOGIN:
                System.out.println("LOGIN");
                if (user != null) {
                    TextMessage sendMessage = new TextMessage();
                    sendMessage.setType(Type.MSG_STATUS);
                    sendMessage.setText("already logged in");
                    try {
                        send(sendMessage);
                    } catch (ProtocolException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    LoginMessage loginMessage = (LoginMessage) msg;
                    try {

                    } catch (ClassCastException e) {
//                        ...
                    }

                    user = new User();
                    user.setName(loginMessage.getLogin());
                    user.setPassword(loginMessage.getPassword());
                    user = server.getUserStore().getUser(loginMessage.getLogin(),
                            loginMessage.getPassword());
                    if (user == null) {
                        user = server.getUserStore().addUser(user);
                    }
                    server.getActiveUsers().put(user.getId(), this);
                    System.out.println("LOGIN SUCCESS");
                }
                break;
            case MSG_TEXT:
                if (user == null) {
                    TextMessage sendMessage = new TextMessage();
                    sendMessage.setType(Type.MSG_STATUS);
                    sendMessage.setText("Log in first");
                    try {
                        send(sendMessage);
                    } catch (ProtocolException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg.setSenderId(user.getId());

                    TextMessage textMessage = (TextMessage) msg;

                    Chat chat = server.getMessageStore()
                            .getChatById(textMessage.getChatId());

                    msg = server.getMessageStore()
                            .addMessage(textMessage.getChatId(), msg);

                    for (Long userId: chat.getUsers()) {
                        if (server.getActiveUsers().containsValue(userId)) {
                            try {
                                server.getActiveUsers().get(userId).send(msg);
                                //NPE
                            } catch (ProtocolException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case MSG_INFO:
                break;
            case MSG_CHAT_LIST:
                break;
            case MSG_CHAT_CREATE:
                break;
            case MSG_CHAT_HIST:
                break;
            default:
                break;
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
