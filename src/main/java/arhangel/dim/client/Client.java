package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.ChatMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

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

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;
    private User user;

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
        if (msg.getType() == Type.MSG_STATUS) {
            StatusMessage status = (StatusMessage) msg;
            log.info(status.getText());
            if (status.getStatusCode() == StatusCode.LoggingInSucceed) {
                user.setName(status.getUserName());
                user.setId(status.getUserId());
            }
            if (status.getStatusCode() == StatusCode.LoggingInFailed) {
                System.out.println("Can't recognize you, try to login again");
            }
            if (status.getStatusCode() == StatusCode.UnknownCommand) {
                System.out.println("Got the command of unknown type, see the list of commands here /help");
            }
        }
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (tokens.length < 3) {
                    log.error("Expected 3 tokens, got {}", tokens.length);
                    System.out.println("Few arguments, type /help for more info");
                    return;
                }
                LoginMessage msg = new LoginMessage(tokens[1], tokens[2]);
                msg.setType(Type.MSG_LOGIN);
                send(msg);
                break;
            case "/help":
                System.out.println("Hello comrade!");
                System.out.println("Behold! See the commands list!");
                System.out.println("/login <your_login> <your_password> | login to chat");
                System.out.println("/info [id] | information about user with id = [id]");
                System.out.println("/info | information about you");
                System.out.println("/chat_list | get the list of chats (you must be logged in)");
                System.out.println("/chat_create <user_id list>{format: [id_1],[id_2],...,[id_n]} " +
                        "| creates the new chat with <user_id list> users");
                System.out.println("/chat_history [chat_id] | show messages from chat with id = [chat_id]");
                System.out.println("/text [chat_id] <message> | send <message> to chat with chat_id = [chat_id]");
                System.out.println("<xxx> means text and [xxx] means integer");
                System.out.println("Enjoy!");
                break;
            case "/text":
                if (tokens.length < 3) {
                    log.error("Expected 3 tokens, got {}", tokens.length);
                    System.out.println("Few arguments, type /help for more info");
                    return;
                }

                if (user != null) {
                    Long chatId;
                    try {
                        chatId = Long.parseUnsignedLong(tokens[1]);
                    } catch (NumberFormatException e) {
                        log.error("Wrong format of chat_id, must be long");
                        e.printStackTrace();
                        return;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 2; i < tokens.length; ++i) {
                        stringBuilder.append(tokens[i]);
                        if (i + 1 != tokens.length) {
                            stringBuilder.append(" ");
                        }
                    }
                    String text = stringBuilder.toString();
                    ChatMessage sendMessage = new ChatMessage(chatId, text);
                    sendMessage.setType(Type.MSG_TEXT);
                    sendMessage.setSenderId(user.getId());
                    send(sendMessage);
                } else {
                    System.out.println("You must be logged in");
                }
                break;
            case "/info":
                if (user != null) {
                    Long userId = user.getId();
                    if (tokens.length > 1) {
                        try {
                            userId = Long.parseUnsignedLong(tokens[1]);
                        } catch (NumberFormatException e) {
                            log.error("Wrong format of user_id, must be long");
                            e.printStackTrace();
                            return;
                        }
                    }
                    InfoMessage info = new InfoMessage();
                    info.setType(Type.MSG_INFO);
                    info.setId(user.getId());
                    info.setSenderId(userId);
                    send(info);
                } else {
                    System.out.println("You must be logged in");
                }
                break;
            case "/chat_create":
                if (user != null) {
                    if (tokens.length < 2) {
                        System.out.println("Not enough arguments");
                        return;
                    }
                    /*Добавляем в конец списка, лучше ArrayList*/
                    List<Long> participants = new ArrayList<>();
                    String[] users = tokens[1].split(",");
                    for (String strUser : users) {
                        try {
                            participants.add(Long.parseUnsignedLong(strUser));
                        } catch (NumberFormatException e) {
                            log.error("Wrong format of user_id, must be long");
                            e.printStackTrace();
                            return;
                        }
                    }
                    participants.add(user.getId());
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage(participants);
                    chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                    chatCreateMessage.setSenderId(user.getId());
                    send(chatCreateMessage);
                } else {
                    System.out.println("You must be logged in");
                }
                break;
            case "/chat_history":
                if (user != null) {
                    if (tokens.length < 2) {
                        System.out.println("Not enough arguments");
                        return;
                    }
                    Long chatId;
                    try {
                        chatId = Long.parseUnsignedLong(tokens[1]);
                    } catch (NumberFormatException e) {
                        log.error("Wrong format of chat_id, must be long");
                        e.printStackTrace();
                        return;
                    }
                    ChatHistMessage chatHistMessage = new ChatHistMessage(chatId);
                    chatHistMessage.setType(Type.MSG_CHAT_HIST);
                    chatHistMessage.setSenderId(user.getId());
                    send(chatHistMessage);

                } else {
                    System.out.println("You must be logged in");
                }
                break;
            case "/chat_list":
                if (user != null) {
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setType(Type.MSG_CHAT_LIST);
                    chatListMessage.setSenderId(user.getId());
                    send(chatListMessage);
                } else {
                    System.out.println("You must be logged in");
                }
                break;
            default:
                log.error("Invalid input: " + line);
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
        try {
            in.close();
            out.close();
            socketThread.interrupt();
        } catch (IOException e) {
            log.error("Can't close in or out in Client");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        // Пользуемся механизмом контейнера
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }
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
