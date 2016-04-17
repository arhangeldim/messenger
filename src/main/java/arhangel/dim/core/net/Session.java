package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import arhangel.dim.client.Client;
import arhangel.dim.commandHandler.ChatCreateHandler;
import arhangel.dim.commandHandler.LoginHandler;
import arhangel.dim.commandHandler.TextHandler;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Session(InputStream in, OutputStream out, Protocol protocol){
        this.in = in;
        this.out = out;
        this.protocol = protocol;
    }
    public void setUser(Long id, String name){
        user = new User();
        user.setId(id);
        user.setName(name);
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
        switch (type) {
            case MSG_TEXT:
                if (user == null) {
                    StatusMessage statmesg = new StatusMessage();
                    statmesg.setText("Unlogged users cannot send messages. Your message is not sent. Log in to send messages.");
                    try {
                        send(statmesg);
                    } catch (Exception e) {
                        log.error("Failed to send status msg");
                    }
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
                    StatusMessage statmesg = new StatusMessage();
                    statmesg.setText("You are already logged in as "+user.getName());
                    try {
                        send(statmesg);
                    } catch (Exception e) {
                        log.error("Failed to send status msg");
                    }
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
                    StatusMessage statmesg = new StatusMessage();
                    statmesg.setText("Unlogged users cannot create chats. Your chat is not created. Log in to create chats.");
                    try {
                        send(statmesg);
                    } catch (Exception e) {
                        log.error("Failed to send status msg");
                    }
                } else {
                    ChatCreateHandler chatCrHandler = new ChatCreateHandler();
                    try {
                        chatCrHandler.execute(this, msg);
                    } catch (CommandException e) {
                        log.error("Failed to execute chatCrHandler");
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

    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }
}
