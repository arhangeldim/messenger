package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import arhangel.dim.client.Client;
import arhangel.dim.commandHandler.*;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static arhangel.dim.server.Server.map;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements Runnable, ConnectionHandler {

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    static Logger log = LoggerFactory.getLogger(Session.class);
    private User user;
    private Protocol protocol;
    public Session(Socket socket, Protocol protocol){
        try{
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            log.error("Failed to init socket");
        }

        this.protocol = protocol;
    }
    public void setUser(Long id, String name){
        user = new User();
        user.setId(id);
        user.setName(name);
    }
    public User getUser() {
        return user;
    }

    public void run(){
        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                if (read > 0) {
                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                    onMessage(msg);
                }
            } catch (Exception e) {
                log.error("Failed to process user session: {}", e);
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                try {
                    in.close();
                    out.close();
                    log.info("Closing sockets");
                } catch(IOException e) {
                    //ignore
                }

            }
        }

    }

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public void send(Message msg) throws ProtocolException, IOException {
        // TODO: Отправить клиенту сообщение
        out.write(protocol.encode(msg));
        out.flush();
    }

    public void onMessage(Message msg){
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
        Type type = msg.getType();
        if (user != null) {
            msg.setSenderId(user.getId());
        }
        try {
            map.get(type).execute(this, msg);
        } catch (CommandException e) {
            log.error("Failed to execute regHandle");
        }

        switch (type) {
            case MSG_REGISTER:
                RegistryHandler regHandle = new RegistryHandler();
                try {
                    regHandle.execute(this, msg);
                } catch (CommandException e) {
                    log.error("Failed to execute regHandle");
                }
                break;
            case MSG_TEXT:
                if (user == null) {
                    notLoggedIn("Unlogged users cannot send messages. Your message is not sent. Log in to send messages.");
                } else {
                    TextHandler textHandler = new TextHandler();
                    try {
                        textHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute textHandler");
                    }
                }
                break;
            case MSG_LOGIN:
                if (user != null) {
                    notLoggedIn("You are already logged in as "+user.getName());
                } else {
                    LoginHandler loginHandler = new LoginHandler();
                    try {
                        loginHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute loginHandler");
                    }
                }
                break;
            case MSG_CHAT_CREATE:
                if (user == null) {
                    notLoggedIn("Unlogged users cannot create chats. Your chat is not created. Log in to create chats.");
                } else {
                    ChatCreateHandler chatCrHandler = new ChatCreateHandler();
                    try {
                        chatCrHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute chatCrHandler");
                    }
                }
                break;
            case MSG_CHAT_HIST:
                if (user == null) {
                    notLoggedIn("Request available only to logged in users.");
                } else {
                    ChatHistHandler chatHistHandler = new ChatHistHandler();
                    try {
                        chatHistHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute chatHistHandler");
                    }
                }
                break;
            case MSG_CHAT_LIST:
                if (user == null) {
                    notLoggedIn("Request available only to logged in users.");
                } else {
                    ChatListHandler chatListHandler = new ChatListHandler();
                    try {
                        chatListHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute chatListHandler");
                    }
                }
                break;
            case MSG_INFO:
                if (user == null) {
                    notLoggedIn("Request available only to logged in users.");
                } else {
                    InfoHandler infoHandler = new InfoHandler();
                    try {
                        infoHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute InfoHandler");
                    }
                }
                break;
            default:
                StatusMessage statmsg = new StatusMessage();
                statmsg.setText("Wrong type, try again");
                try {
                    send(statmsg);
                } catch (Exception e) {
                    log.error("Failed to send status msg");
                }
        }
    }
    public void notLoggedIn(String text) {
        StatusMessage statmesg = new StatusMessage();
        statmesg.setText(text);
        try {
            send(statmesg);
        } catch (Exception e) {
            log.error("Failed to send status msg");
        }
    }
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }
}
