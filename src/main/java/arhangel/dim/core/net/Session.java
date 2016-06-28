package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatHistoryResultMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.MessageDao;
import arhangel.dim.core.store.UserDao;
import arhangel.dim.server.Server;

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
                            if (session.getUser().getName().equals(loginMessage.getLogin())) {
                                statusMessage = new StatusMessage();
                                statusMessage.setStatus("User " + loginMessage.getLogin() + " already logged in");
                                statusMessage.setType(Type.MSG_STATUS);
                                send(statusMessage);
                                return;
                            }
                        }
                    }

                    User foundUser = userDao.getUser(loginMessage.getLogin(), loginMessage.getPassword());

                    if (foundUser == null) {
                        User newUser = new User();
                        newUser.setName(loginMessage.getLogin());
                        newUser.setPassword(loginMessage.getPassword());
                        newUser = userDao.addUser(newUser);
                        statusMessage = new StatusMessage();
                        statusMessage.setStatus("User " + newUser.getName() + " was created");
                        this.setUser(newUser);
                        statusMessage.setType(Type.MSG_STATUS);
                        send(statusMessage);
                        break;
                    } else if (foundUser.getPassword() == null) {
                        statusMessage = new StatusMessage();
                        statusMessage.setStatus("Wrong password for " + foundUser.toString());
                        statusMessage.setType(Type.MSG_STATUS);
                        send(statusMessage);
                        break;
                    }
                    user = foundUser;
                    this.setUser(user);
                    statusMessage = new StatusMessage();
                    statusMessage.setStatus("Logged in");
                    statusMessage.setSenderId(user.getId());
                    statusMessage.setType(Type.MSG_STATUS);
                    this.send(statusMessage);
                    break;

                case MSG_TEXT:
                    if (this.user == null) {
                        break;
                    }
                    //Добавление сообщения в базу
                    TextMessage textMsg = (TextMessage) msg;
                    textMsg.setSenderId(user.getId());
                    messageDao.addMessage(textMsg);

                    //Получение списка пользователей в чате
                    List<Long> usersIdList = messageDao.getUsersIdByChatId(textMsg.getChatId());
                    StatusMessage statusMessageText = new StatusMessage();
                    statusMessageText.setStatus(textMsg.getText());
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
                    if (this.user == null) {
                        break;
                    }
                    ChatListMessage chatListMessage = (ChatListMessage) msg;

                    List<Long> chatsIdList = messageDao.getChatsIdByUserId(user.getId());
                    ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                    chatListResultMessage.setChatIds(chatsIdList);

                    send(chatListResultMessage);
                    break;

                case MSG_CHAT_CREATE:
                    if (this.user == null) {
                        break;
                    }
                    ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;

                    List<Long> userIdList = chatCreateMessage.getParticipantIds();
                    Long createdChatId = messageDao.addChat(user.getId(), userIdList).getId();
                    StatusMessage chatWasCreated = new StatusMessage();
                    chatWasCreated.setStatus("Chat with id = " + createdChatId + " was created");
                    chatWasCreated.setType(Type.MSG_STATUS);
                    send(chatWasCreated);
                    break;

                case MSG_CHAT_HIST:
                    if (this.user == null) {
                        break;
                    }
                    ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) msg;

                    List<TextMessage> allMessages;
                    allMessages = messageDao.getChatById(chatHistoryMessage.getChatId()).getMessages();
                    ChatHistoryResultMessage historyResult = new ChatHistoryResultMessage();
                    historyResult.setMessagesText(allMessages);
                    send(historyResult);
                    break;

                case MSG_INFO:
                    InfoMessage infoMessage = (InfoMessage) msg;
                    if (infoMessage.getUserId() == 0L) {
                        infoMessage.setUserId(user.getId());
                    }
                    User infoUser = userDao.getUserById(infoMessage.getUserId());

                    InfoResultMessage infoResultMessage = new InfoResultMessage();
                    infoResultMessage.setType(Type.MSG_INFO_RESULT);
                    infoResultMessage.setInfo(infoUser.getName() + " is found");
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
