package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginCommand;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.RegisterCommand;
import arhangel.dim.core.messages.TextCommand;
import arhangel.dim.server.Server;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler {

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean userAuthenticated() {
        return (user != null);
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

    private Server server;

    public Server getServer() {
        return server;
    }

    public Session(Server server) {
        this.server = server;
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        // TODO: Отправить клиенту сообщение
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(server.getProtocol().encode(msg));
        outputStream.flush();
    }

    @Override
    public void onMessage(Message msg) {
        // TODO: Пришло некое сообщение от клиента, его нужно обработать
        switch (msg.getType()) {
            case MSG_REGISTER:
                RegisterCommand registerCommand = new RegisterCommand();
                try {
                    registerCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_LOGIN:
                LoginCommand loginCommand = new LoginCommand();
                try {
                    loginCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_TEXT:
                TextCommand textCommand = new TextCommand();
                try {
                    textCommand.execute(this, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                return;
        }
    }

    @Override
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }
}
