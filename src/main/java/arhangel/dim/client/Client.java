package arhangel.dim.client;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.MessageException;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Клиент для тестирования серверного приложения
 */
@SuppressWarnings("ALL")
public class Client implements ConnectionHandler {

    /**
     * Механизм логирования позволяет более гибко управлять записью данных в лог (консоль, файл и тд)
     * */
    static Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * Протокол, хост и порт инициализируются из конфига
     *
     * */
    private Protocol protocol;
    private int port;
    private String host;
    private User user;

    private String helpText = "\nFor everyone:\n" +
            "'/help' for help\n" +
            "'/login <name> <password>' to login\n" +

            "\nOnly for logged in:\n" +
            "'/text <chat_id> <message>' to sent message to chat with given id\n" +
            "'/info' to find out info about yourself\n" +
            "'/info <user_id>' to find out info about user with given id\n" +
            "'/chat_list' to get list of your chats\n" +
            "'/chat_create <user_id>' to create chat with user with given id or to open it, if it already exists\n" +
            "'/chat_create <user_id list>' to create chat with users with given ids (always new chat)\n" +
            "'/chat_history <chat_id>' to git history of chat with given id";

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        /**
         * Инициализируем поток-слушатель. Синтаксис лямбды скрывает создание анонимного класса Runnable
         */
        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
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

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {
        log.info("Message received: {}", msg);
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        try {
            switch (cmdType) {
                case "/login":
                    if (tokens.length < 3) {
                        throw new MessageException("You should send your login and password to login");
                    }
                    if (tokens.length > 3) {
                        throw new MessageException("Login and password should be single words");
                    }
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setType(Type.MSG_LOGIN);
                    loginMessage.setLogin(tokens[1]);
                    loginMessage.setPassword(tokens[2]);
                    send(loginMessage);
                    break;

                case "/help":
                    if (tokens.length > 1) {
                        throw new MessageException("There should be no parameters for help command");
                    }
                    System.out.println(helpText);
                    break;

                case "/text":
                    if (tokens.length == 1) {
                        throw new MessageException("You should type chat_id and your message");
                    }
                    if (tokens.length == 2) {
                        throw new MessageException("It's not allowed to send empty message");
                    }
                    TextMessage textMessage = new TextMessage();
                    textMessage.setType(Type.MSG_TEXT);
                    textMessage.setChatId(Long.parseLong(tokens[1]));
                    String messageText = new String();
                    for (int i = 2; i < tokens.length; i++) {
                        messageText += tokens[i];
                        messageText += " ";
                    }
                    textMessage.setText(messageText);
                    send(textMessage);
                    break;

                case "/chat_list":
                    if (tokens.length > 1) {
                        throw new MessageException("There should be no parameters for chat_list command'");
                    }
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setType(Type.MSG_CHAT_LIST);
                    send(chatListMessage);
                    break;

                case "/info":
                    if (tokens.length > 2) {
                        throw new MessageException("Invalid input for info command");
                    }
                    InfoMessage infoMessage = new InfoMessage();
                    infoMessage.setType(Type.MSG_INFO);
                    if (tokens.length == 2) {
                        infoMessage.setUserId(Long.parseLong(tokens[1]));
                    } else {
                        infoMessage.setUserId(0L);
                    }
                    send(infoMessage);
                    break;

                case "/chat_create":
                    if (tokens.length == 1) {
                        throw new MessageException("Type user_ids, witch you want to start chat with");
                    }
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                    chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                    List<Long> userIds = new ArrayList();
                    for (int i = 1; i < tokens.length; i++) {
                        userIds.add(Long.parseLong(tokens[i]));
                    }
                    chatCreateMessage.setParticipantIds(userIds);
                    send(chatCreateMessage);
                    break;

                case "/chat_history":
                    if (tokens.length == 1) {
                        throw new MessageException("Type chat_id to get it's history");
                    }
                    if (tokens.length > 2) {
                        throw new MessageException("Don't type anything, except chat_id");
                    }
                    ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
                    chatHistoryMessage.setType(Type.MSG_CHAT_HIST);
                    chatHistoryMessage.setChatId(Long.parseLong(tokens[1]));
                    send(chatHistoryMessage);
                    break;

                default:
                    System.out.println("No such command: " + cmdType);

            }
        } catch (MessageException | NumberFormatException e) {
            log.error(e.getMessage() + "\nType '/help' for help\n");
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
    }

    @Override
    public void close() {
        // TODO: написать реализацию. Закройте ресурсы и остановите поток-слушатель
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        // Пользуемся механизмом контейнера
//        try {
//            Container context = new Container("client.xml");
//            client = (Client) context.getByName("client");
//
//        } catch (InvalidConfigurationException e) {
//            log.error("Failed to create client", e);
//            return;
//        }
        client = new Client();
        client.setPort(19000);
        client.setHost("localhost");
        Protocol protocol = new BinaryProtocol();
        client.setProtocol(protocol);

        try {
            client.initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }
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
