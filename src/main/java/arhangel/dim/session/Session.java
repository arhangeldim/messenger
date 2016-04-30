package arhangel.dim.session;

import java.io.IOException;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.MessagesHandler;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.server.Server;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public abstract class Session implements ConnectionHandler {

    protected MessagesHandler messagesHandler;

    protected Channel channel;

    static Logger log = LoggerFactory.getLogger(Session.class);

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    protected User user;


    public Session(Channel channel, Server server) {
        this.channel = channel;
        messagesHandler = new MessagesHandler(server);
    }

    @Override
    public abstract void send(Message msg) throws ProtocolException, IOException;

    @Override
    public void onMessage(Message msg) {
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
        log.info("message received: ", msg);

        try {
            messagesHandler.execute(this, msg);
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
