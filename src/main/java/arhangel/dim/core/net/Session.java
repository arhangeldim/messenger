package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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

    private InputStream in;
    private OutputStream out;

    public Session(Socket socket, Protocol protocol) {
        this.protocol = protocol;
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[1024 * 500];
            int readBytes = 0;
            try {
                readBytes = in.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (readBytes > 0) {
                //  executor.submit(new Worker(server, buf));
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
                    UserDao userDao = new UserDao();

                    User founduser = userDao.getUser(loginMessage.getLogin(), loginMessage.getPassword());

                    if (founduser == null) {
                        founduser = new User(loginMessage.getLogin(), loginMessage.getPassword());
                        founduser = userDao.addUser(founduser);
                    }
                    user = founduser;
                    StatusMessage statusMessage = new StatusMessage("You logged in as " + user.getLogin());
                    this.send(statusMessage);
                    break;
                default: throw new CommandException("Несуществующая команда сервера");
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
