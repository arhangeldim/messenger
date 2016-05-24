package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatHistoryResultMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

public class Client implements ConnectionHandler {

    private Long userId;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private Protocol protocol;
    private int port;
    private String host;

    private Thread socketThread;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Protocol getProtocol() {
        return protocol;
    }

    public InputStream getIn() {
        return in;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void initSocket() throws IOException {
        socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 500];
            log.info("Starting listener thread...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Здесь поток блокируется на ожидании данных
                    int read = in.read(buf);
                    if (read > 0) {

                        // По сети передается поток байт, его нужно раскодировать с помощью протокола
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (Exception e) {
                    log.error("Failed to process connection: {}", e);
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
        socketThread.start();
    }

    @Override
    public void onMessage(Message msg) {
        switch (msg.getType()) {
            case MSG_STATUS:
                StatusMessage statusMsg = (StatusMessage) msg;
                if (statusMsg.getStatus().equals("Logged in")) {
                    this.setUserId(statusMsg.getSenderId());
                    log.info("You logged in as user with id = " + statusMsg.getSenderId().toString());
                } else if (statusMsg.getStatus().equals("Chat created")) {
                    log.info("You created a chat");
                } else {
                    log.info("Recieved: " + statusMsg.getStatus());
                }
                break;

            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage resultMessage = (ChatListResultMessage) msg;
                log.info(resultMessage.getChatsIdList().toString());
                break;

            case MSG_CHAT_HIST_RESULT:
                ChatHistoryResultMessage resultHistory = (ChatHistoryResultMessage) msg;
                log.info(resultHistory.getMessagesInChatId().toString());
                break;

            case MSG_INFO_RESULT:
                InfoResultMessage infoResult = (InfoResultMessage) msg;
                log.info("About user: "
                        + "Login: " + infoResult.getLogin()
                        + "  Password: " + infoResult.getPassword());
                break;

            default: log.error("Unknown recieved message");
        }
    }

    public boolean checkArgNum(String[] strings, int num) {
        if (strings.length < num) {
            log.error("Not enough arguments");
            return false;
        } else if (strings.length > num) {
            log.error("Too many arguments");
            return false;
        }
        return true;
    }

    public boolean isLoggedIn() {
        if (userId == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (!checkArgNum(tokens, 3)) {
                    return false;
                }
                LoginMessage msg = new LoginMessage();
                msg.setType(Type.MSG_LOGIN);
                msg.setLogin(tokens[1]);
                msg.setPassword(tokens[2]);
                send(msg);
                return true;

            case "/text":
                if (!checkArgNum(tokens, 3)) {
                    return false;
                }
                TextMessage textMessage = new TextMessage();
                if (!isLoggedIn()) {
                    log.error("Can't send a message while not logged in");
                    return false;
                }
                textMessage.setSenderId(this.getUserId());
                textMessage.setType(Type.MSG_TEXT);
                textMessage.setChatId(Long.parseLong(tokens[1]));
                textMessage.setText(tokens[2]);
                send(textMessage);
                return true;

            case "/help":
                log.info("Messenger v 1.0" +
                        "Type:" +
                        "1) /login to log in" +
                        "2) /text to send a message" +
                        "3) /chat_list to recieve chats you are in");
                return true;

            case "/info":
                if (!isLoggedIn()) {
                    log.error("Anonymous can't get info about users");
                    return false;
                }

                InfoMessage infomsg = new InfoMessage();
                infomsg.setType(Type.MSG_INFO);
                
                if (tokens.length == 1) {
                    log.debug("Self-information case");
                    infomsg.setUsrId(this.getUserId());
                } else {
                    log.info(tokens[1]);
                    infomsg.setUsrId(Long.parseLong(tokens[1]));
                }
                send(infomsg);
                return true;

            case "/chat_list":
                if (!isLoggedIn()) {
                    log.error("Can't check chat list for anonymous");
                    return false;
                }
                if (tokens.length > 1) {
                    log.error("Too many arguments");
                }
                ChatListMessage chatListMessage = new ChatListMessage(this.userId);
                send(chatListMessage);
                return true;

            case "/chat_create":
                if (!isLoggedIn()) {
                    log.error("Can't check chat list for anonymous");
                    return false;
                }
                if (!checkArgNum(tokens, 2)) {
                    return false;
                }

                String[] users = tokens[1].split(",");
                List<Long> userIdList = new ArrayList<>();
                for (String s : users) userIdList.add(Long.valueOf(s));

                ChatCreateMessage chatCreateMessage = new ChatCreateMessage(userIdList);
                send(chatCreateMessage);
                return true;

            case "/chat_history":
                if (!isLoggedIn()) {
                    log.error("Can't get chat history for anonymous");
                    return false;
                }
                if (!checkArgNum(tokens, 2)) {
                    return false;
                }

                ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage(Long.parseLong(tokens[1]));
                send(chatHistoryMessage);
                return true;

            default:
                log.error("Unknown input command: " + line);
                return false;
        }
    }

    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void close() throws IOException, InterruptedException {
        if ( !socket.isClosed()) {
            socket.close();
        }
        if (!socketThread.isInterrupted()) {
            socketThread.interrupt();
            socketThread.join();
        }
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }
        try {
            client.initSocket();

            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            byte[] buf = new byte[1024 * 500];

            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    if (!client.processInput(input)) {
                        continue;
                    }
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }

//                int readBytes = client.getIn().read(buf);
//                Message msg = client.getProtocol().decode(buf);

            }
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
