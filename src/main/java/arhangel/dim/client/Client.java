package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatHistResultMessage;
import arhangel.dim.core.messages.ChatInfoMessage;
import arhangel.dim.core.messages.ChatInfoResultMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.ErrorMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.utils.ParseUtils;
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
     */
    static Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * Протокол, хост и порт инициализируются из конфига
     */
    private Protocol protocol;
    private int port;
    private String host;

    private String login;
    private String password;

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private Socket socket;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void initSocket() throws IOException {
        socket = new Socket(host, port);
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
                    Thread.currentThread().interrupt();
                }
            }
        });

        socketThread.start();

        if (login != null && password != null) {
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setLogin(login);
            loginMessage.setPassword(password);
            try {
                if (!socket.isClosed()) {
                    send(loginMessage);
                }
            } catch (ProtocolException e) {
                System.out.println("Authentication failed!");
            } catch (SocketException e) {
                System.out.println("Connection problems. Authentication failed!");
            }
        }
    }

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {

        log.info("Message received: {}", msg);

        switch (msg.getType()) {
            case MSG_STATUS:
                StatusMessage msgStatus = (StatusMessage) msg;
                System.out.println(msgStatus.getText());
                break;
            case MSG_CHAT_LIST_RESULT:
                ChatListResultMessage msgChatListResult = (ChatListResultMessage) msg;
                if (msgChatListResult.getChatIds().size() == 0) {
                    System.out.println("You have no chats yet.");
                } else {
                    System.out.println("Your chats: " + String.join(",", msgChatListResult.getChatIds().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList())));
                }
                break;
            case MSG_INFO_RESULT:
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                StringBuilder sb = new StringBuilder();
                sb.append("Info.\n")
                        .append("Name: ").append(infoResultMessage.getName()).append("\n")
                        .append("Id: ").append(infoResultMessage.getUserId()).append("\n")
                        .append("Chats: ").append(String.join(",", infoResultMessage.getChats().stream()
                        .map(Object::toString).collect(Collectors.toList())))
                        .append(".");

                System.out.println(sb.toString());
                break;
            case MSG_CHAT_HIST_RESULT:
                ChatHistResultMessage chatHistResultMessage = (ChatHistResultMessage) msg;
                chatHistResultMessage.getMessages().stream()
                        .forEach(tm -> System.out.println(tm.getSenderId() + ":" + tm.getText()));
                break;
            case MSG_CHAT_INFO_RESULT:
                ChatInfoResultMessage chatInfoResultMessage = (ChatInfoResultMessage) msg;

                System.out.println("Users of chat " + chatInfoResultMessage.getChatId() + ": " +
                        String.join(",", chatInfoResultMessage.getUserIds().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList())));
                break;
            case MSG_ERROR:
                ErrorMessage errorMessage = (ErrorMessage) msg;
                System.out.println(errorMessage.getText());
                close();
                break;
            default:
                log.error("unsupported type of message");
                break;
        }

    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        try {
            String[] tokens = line.split(" ");
            log.info("Tokens: {}", Arrays.toString(tokens));
            String cmdType = tokens[0];
            switch (cmdType) {
                case "/login":
                    if (tokens.length != 3) {
                        throw new WrongArgumentsNumberException();
                    }
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setType(Type.MSG_LOGIN);
                    loginMessage.setLogin(tokens[1]);
                    loginMessage.setPassword(tokens[2]);
                    send(loginMessage);
                    break;
                case "/chat_list":
                    if (tokens.length != 1) {
                        throw new WrongArgumentsNumberException();
                    }
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setType(Type.MSG_CHAT_LIST);
                    send(chatListMessage);
                    break;
                case "/chat_create":
                    if (tokens.length < 2) {
                        throw new WrongArgumentsNumberException();
                    }
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                    chatCreateMessage.setUserIds(ParseUtils
                            .stringArrToLongList(line.replace(tokens[0], "").replaceAll("[\\s]+", "").split(",")));
                    send(chatCreateMessage);
                    break;
                case "/chat_history":
                    if (tokens.length != 2) {
                        throw new WrongArgumentsNumberException();
                    }
                    ChatHistMessage chatHistMessage = new ChatHistMessage();
                    chatHistMessage.setChatId(Long.parseLong(tokens[1]));
                    send(chatHistMessage);
                    break;
                case "/help":
                    System.out.println(helpInfo());
                    break;
                case "/text":
                    if (tokens.length < 3) {
                        throw new WrongArgumentsNumberException();
                    }
                    TextMessage sendMessage = new TextMessage();
                    sendMessage.setType(Type.MSG_TEXT);
                    sendMessage.setChatId(Long.parseLong(tokens[1]));
                    sendMessage.setText(line.replace(tokens[0] + " " + tokens[1] + " ", ""));
                    send(sendMessage);
                    break;
                case "/info":
                    if (tokens.length > 2) {
                        throw new WrongArgumentsNumberException();
                    }
                    InfoMessage infoMessage = new InfoMessage();
                    infoMessage.setType(Type.MSG_INFO);
                    if (tokens.length == 1) {
                        infoMessage.setTarget(-1L);
                    } else {
                        infoMessage.setTarget(Long.parseLong(tokens[1]));
                    }
                    send(infoMessage);
                    break;
                case "/chat_info":
                    if (tokens.length != 2) {
                        throw new WrongArgumentsNumberException();
                    }
                    ChatInfoMessage chatInfoMessage = new ChatInfoMessage();
                    chatInfoMessage.setChatId(Long.parseLong(tokens[1]));
                    send(chatInfoMessage);
                    break;
                case "/reconnect":
                    if (tokens.length != 1) {
                        throw new WrongArgumentsNumberException();
                    }
                    close();
                    initSocket();
                    break;
                default:
                    invalidInput(line);
                    log.error("Invalid input: " + line);
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | WrongArgumentsNumberException e) {
            invalidInput(line);
        }
    }

    private void invalidInput(String line) {
        System.out.println("Invalid input: " + line);
        System.out.println(helpInfo());
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public synchronized void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        byte[] bytes = protocol.encode(msg);
//        Integer size = bytes.length;
        byte[] size = ByteBuffer.allocate(4).putInt(bytes.length).array();
        out.write(size);
        out.write(bytes);
        out.flush(); // принудительно проталкиваем буфер с данными
    }

    @Override
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socketThread != null && !socketThread.isInterrupted()) {
            socketThread.interrupt();
        }
    }

    private String helpInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: /command [arg...]").append("\n");
        sb.append("Available commands:").append("\n");
        sb.append("\t/login <login> <password>").append("\n");
        sb.append("\t/chat_list").append("\n");
        sb.append("\t/chat_create <user_id...>").append("\n");
        sb.append("\t/text <chat_id> <text>").append("\n");
        sb.append("\t/chat_history <chat_id>").append("\n");
        sb.append("\t/info [<user_id>]").append("\n");
        sb.append("\t/chat_info [<chat_id>]").append("\n");
        sb.append("\t/reconnect");

        return sb.toString();
    }

    public void start() {
        try {
            initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$");
                String input = scanner.nextLine();
                log.info("Command readed from console");
                if ("q".equals(input)) {
                    return;
                }
                try {
                    processInput(input);
                } catch (ProtocolException | IOException e) {
                    System.out.println("Error occurred during connection to server.");
                }
            }
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            close();
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

        client.start();

    }

    private static class WrongArgumentsNumberException extends RuntimeException {
    }
}
