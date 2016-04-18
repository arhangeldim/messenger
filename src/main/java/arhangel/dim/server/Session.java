package arhangel.dim.server;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    private User user;

    // сокет на клиента
    private Socket socket;

    private MessageStore messageStore;
    private UserStore userStore;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private Protocol protocol;

    private CommandExecutor commandExecutor;

    public Session(Socket socket, Protocol protocol, CommandExecutor commandExecutor) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.protocol = protocol;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        try {
            commandExecutor.handleMessage(msg, this);
        } catch (CommandException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            Thread.currentThread().interrupt();
            in.close();
            out.close();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //TODO
        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                if (read > 0) {
                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                    onMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
