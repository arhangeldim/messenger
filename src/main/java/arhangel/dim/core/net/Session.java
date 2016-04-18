package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.MessageDao;
import arhangel.dim.server.Server;
import com.sun.javafx.scene.control.skin.FXVK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.store.UserDao;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable {

    private User user;
    private Socket socket;
    private Protocol protocol;
    private Server sessionServer;

    private InputStream in;
    private OutputStream out;

    public Session(Socket socket, Server server) {
        this.sessionServer = server;
        this.protocol = server.getProtocol();
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getIn() {
        return in;
    }
    public Socket getSocket() { return this.socket;}
    public Server getSessionServer() { return sessionServer;}
    public void setUser(User user) { this.user = user;}
    public User getUser() { return this.user;}

    @Override
    public void run() {
        while (!getSocket().isClosed()) {
            byte[] buf = new byte[1024 * 500];
            int readBytes = 0;
            try {
                readBytes = in.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (readBytes > 0) {
                Message msg = null;
                try {
                    msg = protocol.decode(buf);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                onMessage(msg);
            }
        }
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        System.out.println(msg.toString());
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        String type = msg.getType().toString();
        try {

            switch (type) {
                case "MSG_LOGIN":
                    LoginMessage loginMessage = (LoginMessage) msg;
                    StatusMessage statusMessage = null;

                    for (Session sess : this.getSessionServer().getSessionList()) {
                        if (sess.getUser() != null) {
                            if (sess.getUser().getLogin().equals(loginMessage.getLogin())) {
                                statusMessage = new StatusMessage("User " + loginMessage.getLogin() + " already logged in");
                                statusMessage.setType(Type.MSG_STATUS);
                                this.send(statusMessage);
                                return;
                            }
                        }
                    }

                    UserDao userDao = new UserDao();
                    User founduser = userDao.getUser(loginMessage.getLogin(), loginMessage.getPassword());

                    if (founduser == null) {
                        User newuser = new User(loginMessage.getLogin(), loginMessage.getPassword());
                        newuser = userDao.addUser(newuser);
                        statusMessage = new StatusMessage("User " + newuser.getLogin()+ " was created");
                        statusMessage.setType(Type.MSG_STATUS);
                        this.send(statusMessage);
                        break;
                    } else if (founduser.getPassword() == null) {
                        statusMessage = new StatusMessage("Wrong password for " + founduser.getLogin());
                        statusMessage.setType(Type.MSG_STATUS);
                        this.send(statusMessage);
                        break;
                    }
                    user = founduser;
                    this.setUser(user);
                    statusMessage = new StatusMessage("Logged in");
                    statusMessage.setSenderId(user.getId());
                    statusMessage.setType(Type.MSG_STATUS);
                    this.send(statusMessage);
                    break;

                case "MSG_TEXT":
                    MessageDao messageDao = new MessageDao();

                    //Добавление сообщения в базу
                    TextMessage textMsg = (TextMessage) msg;
                    messageDao.addMessage(textMsg.getChatId(), textMsg);

                    //Получение списка пользователей в чате
                    List<Long> usersIdList = messageDao.getUsersByChatId(textMsg.getChatId());
                    StatusMessage statusMessageText = new StatusMessage(textMsg.getText());
                    statusMessageText.setType(Type.MSG_STATUS);

                    //Рассылка сообщения всем в чат
                    for (int i = 0; i < this.getSessionServer().getSessionList().size(); i++) {
                        Session session = this.getSessionServer().getSessionList().get(i);
                        if (session.getUser() != null) {
                            if (usersIdList.contains(session.getUser().getId())) {
                                session.send(statusMessageText);
                            }
                        }
                    }

                    break;
                default: throw new CommandException("Unknown server command");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
    }
}
