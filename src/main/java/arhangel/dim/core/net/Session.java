package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;



import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.commands.CommandByMessage;
import arhangel.dim.core.messages.commands.CommandException;
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

    private Server server;

    @Override
    public void send(Message msg) {
        try {
            out.write(server.getProtocol().encode(msg));
            out.flush();
        } catch (IOException | ProtocolException e) {
            Server.getLog().error(e.getMessage());
        }

    }

    @Override
    public void onMessage(Message msg) {
        Server.getLog().debug(msg.toString());
        try {
            CommandByMessage.getCommand(msg.getType()).execute(this, msg);
        } catch (CommandException e) {
            Server.getLog().error(e.getMessage());
        }
    }

    @Override
    public void close() {
        if (user != null) {
            getServer().getActiveUsers().remove(user.getId());
        }
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            Server.getLog().error(e.getMessage());
        }
        Server.getLog().debug("End of session");
    }

    public Session(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            Server.getLog().error(e.getMessage());
        }
    }

    @Override
    public void run() {
        byte [] binMessage = new byte[MAX_MESSAGE_SIZE];
        while (true) {
            try {
                int result = in.read(binMessage);
                if (result == -1 || server.isFinished()) {
                    close();
                    break;
                }
                Message message = server.getProtocol().decode(binMessage);
                onMessage(message);
            } catch (IOException | ProtocolException e) {
                Server.getLog().error(e.getMessage());
                close();
                break;
            }
        }
    }
}
