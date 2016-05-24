package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatHistoryResultMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.MessageDao;
import arhangel.dim.server.Server;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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

    private UserDao userDao;
    private MessageDao messageDao;

    public Session(Socket socket, Server server) throws IOException {
        this.sessionServer = server;
        this.protocol = server.getProtocol();
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Can't open a socket");
        }

        userDao = new UserDao();
        messageDao = new MessageDao();
    }

    public InputStream getIn() {
        return in;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Server getSessionServer() {
        return sessionServer;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024 * 500];
        while (!socket.isClosed()) {
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
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        try {
            switch (msg.getType()) {
                case MSG_LOGIN:
                    LoginMessage loginMessage = (LoginMessage) msg;
                    StatusMessage statusMessage = null;

                    for (Session session : this.getSessionServer().getSessionList()) {
                        if (session.getUser() != null) {
                            if (session.getUser().getLogin().equals(loginMessage.getLogin())) {
                                statusMessage = new StatusMessage(
                                        "User " + loginMessage.getLogin() + " already logged in");
                                statusMessage.setType(Type.MSG_STATUS);
                                send(statusMessage);
                                return;
                            }
                        }
                    }

                    User founduser = userDao.getUser(loginMessage.getLogin(), loginMessage.getPassword());

                    if (founduser == null) {
                        User newuser = new User(loginMessage.getLogin(), loginMessage.getPassword());
                        newuser = userDao.addUser(newuser);
                        statusMessage = new StatusMessage("User " + newuser.getLogin() + " was created");
                        statusMessage.setType(Type.MSG_STATUS);
                        send(statusMessage);
                        break;
                    } else if (founduser.getPassword() == null) {
                        statusMessage = new StatusMessage("Wrong password for " + founduser.getLogin());
                        statusMessage.setType(Type.MSG_STATUS);
                        send(statusMessage);
                        break;
                    }
                    user = founduser;
                    this.setUser(user);
                    statusMessage = new StatusMessage("Logged in");
                    statusMessage.setSenderId(user.getId());
                    statusMessage.setType(Type.MSG_STATUS);
                    this.send(statusMessage);
                    break;

                case MSG_TEXT:
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

                case MSG_CHAT_LIST:
                    ChatListMessage chatListMessage = (ChatListMessage) msg;

                    List<Long> chatsIdList = messageDao.getChatsByUserId(chatListMessage.getUserId());
                    ChatListResultMessage chatListResultMessage = new ChatListResultMessage(chatsIdList);

                    send(chatListResultMessage);
                    break;

                case MSG_CHAT_CREATE:
                    ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;

                    List<Long> userIdList = chatCreateMessage.getUserIdList();
                    Long createdChatId = messageDao.addChat(userIdList);
                    StatusMessage chatWasCreated = new StatusMessage("Chat with id = " + createdChatId + " was created");
                    chatWasCreated.setType(Type.MSG_STATUS);
                    send(chatWasCreated);
                    break;

                case MSG_CHAT_HIST:
                    ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) msg;

                    List<Long> allMessages = messageDao.getMessagesFromChat(chatHistoryMessage.getChatId());
                    ChatHistoryResultMessage historyResult = new ChatHistoryResultMessage(allMessages);
                    send(historyResult);
                    break;

                case MSG_INFO:
                    InfoMessage infoMessage = (InfoMessage) msg;
                    User infoUser = userDao.getUserById(infoMessage.getUsrId());

                    InfoResultMessage infoResultMessage = new InfoResultMessage(infoUser.getId(),infoUser.getLogin(), infoUser.getPassword(), "");
                    infoResultMessage.setType(Type.MSG_INFO_RESULT);
                    send(infoResultMessage);
                    break;

                default: throw new CommandException("Unknown server command");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void close() {
    }
}
