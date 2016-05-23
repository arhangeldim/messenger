package arhangel.dim.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.UserCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;

/**
 * Клиент для тестирования серверного приложения
 */
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
    ClientUser user;
    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public Client() {
        port = 9000;
        host = "localhost";
        protocol = new BinaryProtocol();
    }

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

    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        user = new ClientUser();

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
        if (msg.getType() == Type.MSG_STATUS) {
            StatusMessage status = (StatusMessage) msg;
            if (status.getUsername() != null) {
                user.login(status.getId(), status.getUsername());
                log.info("Successfully logged in with id = " + user.getId());
            } else {
                log.info(status.getText());
            }
        }
        /*
        if (msg.getId() != null) {
            if (user.isLoginned()) {
                if (msg.getId().equals(user.getId())) {
                    log.error("Wrong receiver");
                }
            }
        }
        */
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public boolean processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (tokens.length < 3) {
                    log.error("Not enough arguments");
                    return true;
                } else if (tokens.length > 3) {
                    log.error("Too many arguments");
                    return true;
                }
                LoginMessage message = new LoginMessage(tokens[1], tokens[2]);
                message.setType(Type.MSG_LOGIN);
                //message.setSenderId(user.getId());
                send(message);

                return true;
            case "/help":
                // TODO: реализация
                break;
            case "/text":
                if (tokens.length < 3) {
                    log.error("Not enough arguments for message");
                    return true;
                }
                if (!user.isLoginned()) {
                    log.error("Please, log in");
                    return true;
                }
                Long chatId = Long.parseUnsignedLong(tokens[1]);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 2; i < tokens.length; ++i) {
                    stringBuilder.append(tokens[i]);
                    if (i + 1 != tokens.length) {
                        stringBuilder.append(" ");
                    }
                }
                String text = stringBuilder.toString();
                TextMessage sendMessage = new TextMessage(chatId, text);
                sendMessage.setType(Type.MSG_TEXT);
                sendMessage.setSenderId(user.getId());
                send(sendMessage);
                break;
            case "/info":
                Long userId = user.getId();;
                if (tokens.length == 1) {
                    userId = user.getId();
                } else if (tokens.length == 2) {
                    userId = Long.parseUnsignedLong(tokens[1]);
                } else {
                    log.error("Too many arguments");
                    return true;
                }
                if (!user.isLoginned()) {
                    log.error("Please, log in");
                    return true;
                }
                InfoMessage info = new InfoMessage();
                info.setType(Type.MSG_INFO);
                info.setId(user.getId());
                info.setSenderId(userId);
                send(info);
                return true;
            case "/chat_create":
                if (tokens.length < 2) {
                    System.out.println("Not enough arguments");
                    return true;
                }
                if (!user.isLoginned()) {
                    System.out.println("Please, log in");
                    return true;
                }
                List<Long> participants = new LinkedList<>();
                if (tokens.length == 2) {
                    String[] strUsers = tokens[1].split(",");
                    for (String strUser : strUsers) {
                        participants.add(Long.parseUnsignedLong(strUser));
                    }
                } else {
                    for (int i = 1; i < tokens.length; ++i) {
                        participants.add(Long.parseUnsignedLong(tokens[i]));
                    }
                }
                participants.add(user.getId());
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage(participants);
                chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                chatCreateMessage.setSenderId(user.getId());
                send(chatCreateMessage);
                return true;
            case "/chat_history":
                if (tokens.length < 2) {
                    System.out.println("Not enough arguments");
                    return true;
                }
                Long chatHistId = Long.parseUnsignedLong(tokens[1]);
                ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage(chatHistId);
                chatHistoryMessage.setType(Type.MSG_CHAT_HIST);
                chatHistoryMessage.setSenderId(user.getId());
                send(chatHistoryMessage);
                return true;
            case "/chat_list":
                if (!user.isLoginned()) {
                    System.out.println("Please, log in");
                    return true;
                }
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setType(Type.MSG_CHAT_LIST);
                chatListMessage.setSenderId(user.getId());
                send(chatListMessage);
                return true;
            case "/user_create":
                if (tokens.length < 3) {
                    System.out.println("Not enough arguments");
                    return true;
                }
                String username = tokens[1];
                String password = tokens[2];
                UserCreateMessage userCreateMessage = new UserCreateMessage(username, password);
                userCreateMessage.setType(Type.MSG_USER_CREATE);
                send(userCreateMessage);
                return true;
            default:
                log.error("Unknown input command: " + line);
                return true;
        }
        return true;
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        //System.out.println(protocol);
        protocol.encode(msg);
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
    }

    @Override
    public void close() {
        socketThread.interrupt();
        try {
            in.close();
            out.close();
            socketThread.join();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        Client client = new Client();
        // Пользуемся механизмом контейнера
        /*
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }
        */
        try {
            client.initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("$");
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    if (client.processInput(input)) {
                        continue;
                    }
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }
                byte[] buf = new byte[1024 * 64];
                int readBytes = client.in.read(buf);
                Message msg = client.getProtocol().decode(buf);
            }
        } catch (Exception e) {
            log.error("Application failed.", e);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
