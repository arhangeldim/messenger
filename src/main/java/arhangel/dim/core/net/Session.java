package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import arhangel.dim.client.Client;
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
    public Session(InputStream in, OutputStream out, long id, Protocol protocol){
        this.in = in;
        this.out = out;
        this.protocol = protocol;
        user = new User();
        user.setId(id);
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
                TextHandler textHandler = new TextHandler();
                try {
                    textHandler.execute(this, msg);
                } catch (CommandException e) {
                    log.error("Failed to execute textHandler");
                }
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
