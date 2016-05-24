package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.messages.commands.CommandException;
import arhangel.dim.core.net.BinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.core.messages.commands.CommandByMessage;
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
        try {
            CommandByMessage.getCommand(msg.getType()).execute(null, msg);
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        if (tokens.length == 0) {
            log.error("invalid input");
        }
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                LoginMessage loginMessage = new LoginMessage();
                if (tokens.length < 3) {
                    log.error("Invalid input: " + line);
                }
                loginMessage.setLogin(tokens[1]);
                loginMessage.setType(Type.MSG_LOGIN);
                loginMessage.setPassword(tokens[2]);
                send(loginMessage);
                break;
            case "/help":
                System.out.println("/help - показать список " +
                        "команд и общий хэлп по месседжеру");
                System.out.println("-----------------------------------------");
                System.out.println("/login <логин_пользователя> <пароль>" +
                        "\n" +
                        "/login arhangeldim qwerty");
                System.out.println("залогиниться (если логин не указан, " +
                        "то авторизоваться)");
                System.out.println("-----------------------------------------");
                System.out.println("/info [id]");
                System.out.println("получить всю информацию о пользователе, " +
                        "без аргументов - о себе");
                System.out.println("-----------------------------------------");
                System.out.println("/chat_list");
                System.out.println("получить список чатов " +
                        "пользователя(только для залогиненных пользователей).");
                System.out.println("-----------------------------------------");
                System.out.println("/chat_create <user_id list>");
                System.out.println("создать новый чат, список пользователей " +
                        "приглашенных в чат (только для залогиненных " +
                        "пользователей).");
                System.out.println("/chat_create 1,2,3,4 - создать чат с " +
                        "пользователями id=1, id=2, id=3, id=4");
                System.out.println("/chat_create 3 - создать чат с " +
                        "пользователем id=3, если такой чат уже существует," +
                        " вернуть существующий");
                System.out.println("-----------------------------------------");
                System.out.println("/chat_history <chat_id>");
                System.out.println("список сообщений из указанного чата " +
                        "(только для залогиненных пользователей)");
                System.out.println("-----------------------------------------");
                System.out.println("/text <id> <message>");
                System.out.println("отправить сообщение в заданный чат, чат " +
                        "должен быть в списке чатов пользователя " +
                        "(только для залогиненных пользователей)");
                System.out.println("/text 3 Hello, it's pizza time!" +
                        " - отправить " +
                        "указанное сообщение в чат id=3");
                System.out.println("-----------------------------------------");

                break;
            case "/text":
                if (tokens.length < 2) {
                    log.error("invalid input");
                }
                TextMessage sendMessage = new TextMessage();
                sendMessage.setChatId(Long.parseLong(tokens[1]));
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < tokens.length; ++i) {
                    builder.append(tokens[i]);
                    builder.append(" ");
                }
                sendMessage.setText(builder.toString());
                send(sendMessage);
                break;
            case "/chat_list":
                ChatListMessage msg = new ChatListMessage();
                send(msg);
                break;
            case "/info":
                InfoMessage infoMessage = new InfoMessage();
                if (tokens.length > 1) {
                    try {
                        infoMessage.setUserId(Long.parseLong(tokens[1]));
                    } catch (NumberFormatException e) {
                        log.error("Invalid input: " + line);
                    }
                }
                send(infoMessage);
                break;
            case "/chat_hist":
                if (tokens.length < 2) {
                    log.error("Invalid input: " + line);
                }
                ChatHistMessage chatHistMessage = new ChatHistMessage();
                try {
                    chatHistMessage.setChatId(Long.parseLong(tokens[1]));
                } catch (NumberFormatException e) {
                    log.error("Invalid input: " + line);
                }
                send(chatHistMessage);
                break;
            case "/chat_create":
                if (tokens.length < 3) {
                    log.error("Invalid input: " + line);
                }
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setUsers(new ArrayList<>());
                for (int i = 1; i < tokens.length; ++i) {
                    try {
                        chatCreateMessage.getUsers().add(Long.parseLong(tokens[i]));
                    } catch (NumberFormatException e) {
                        log.error("Invalid input: " + line);
                    }
                }
                send(chatCreateMessage);
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
        // TODO: написать реализацию. Закройте ресурсы и остановите поток-слушатель
    }

    public static void main(String[] args) throws Exception {
        Client client = null;
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
            System.out.println(client);
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
