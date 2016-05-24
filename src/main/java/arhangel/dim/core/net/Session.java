package arhangel.dim.core.net;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.server.Server;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    Map<Type, Command> messageCommand;

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    private User user;
    static final int MAX_MESSAGE_SIZE = 1024;

    // сокет на клиента
    private Socket socket;
    private Server server;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Server getServer() {
        return server;
    }

    //public void setServer(Long id) {
        //this.id = id;
    //}


    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    @Override
    public void send(Message message) throws ProtocolException, IOException {
        if (user == null) {
            return;
        } else {
            byte[] messageToSend = server.getProtocol().encode(message);
            out = socket.getOutputStream();
            out.write(messageToSend);
            out.flush();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            Command command = messageCommand.get(message.getType());
            command.execute(this, message);
        } catch (CommandException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
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


    public Session(Socket socket, Server server) {
        this.server = server;
        messageCommand.put(Type.MSG_LOGIN, new LoginCommand());
        messageCommand.put(Type.MSG_TEXT, new TextCommand());
    }
}
