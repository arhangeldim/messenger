package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.RegisterMessage;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Context;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import sun.rmi.runtime.Log;

import static java.lang.Long.parseLong;

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

        switch (msg.getType()) {
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                System.out.println(statusMessage.getText());
                break;
            default:
                return;
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
            case "/register":
                if (tokens.length != 3) {
                    System.out.println("Expected 2 parameters");
                    return;
                }
                RegisterMessage registerMessage = new RegisterMessage();
                registerMessage.setType(Type.MSG_REGISTER);
                registerMessage.setLogin(tokens[1]);
                registerMessage.setSecret(tokens[2]);
                send(registerMessage);
                break;
            case "/login":
                if (tokens.length != 3) {
                    System.out.println("Expected 2 parameters");
                    return;
                }
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setType(Type.MSG_LOGIN);
                loginMessage.setLogin(tokens[1]);
                loginMessage.setSecret(tokens[2]);
                send(loginMessage);
                break;
            case "/info":
                if (tokens.length > 2) {
                    System.out.println("Expected less than 2 parameters");
                    return;
                } else {
                    InfoMessage infoMessage = new InfoMessage();
                    infoMessage.setType(Type.MSG_INFO);
                    if (tokens.length == 2) {
                        infoMessage.setLogin(tokens[1]);
                    } else {
                        infoMessage.setLogin(null);
                    }
                    send(infoMessage);
                }
                break;
            case "/help":
                // TODO: реализация
                break;
            case "/chat_create":
                if (tokens.length < 2) {
                    System.out.println("Expected at least 1 parameter");
                    return;
                }
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                Set<String> users = new HashSet<>();
                users.addAll(Arrays.asList(tokens).subList(1, tokens.length));
                chatCreateMessage.setUsers(users);
                send(chatCreateMessage);
                break;
            case "/chat_list":
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setType(Type.MSG_CHAT_LIST);
                send(chatListMessage);
                break;
            case "/chat_history":
                if (tokens.length != 2) {
                    System.out.println("Expected 1 argument");
                }
                ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
                chatHistoryMessage.setType(Type.MSG_CHAT_HIST);
                try {
                    chatHistoryMessage.setChatId(parseLong(tokens[1]));
                } catch (Exception e) {
                    System.out.println("Expected number");
                    return;
                }
                send(chatHistoryMessage);
                break;
            case "/text":
                if (tokens.length != 3) {
                    System.out.println("Expected 2 parameters");
                    return;
                }
                // FIXME: пример реализации для простого текстового сообщения
                TextMessage sendMessage = new TextMessage();
                sendMessage.setType(Type.MSG_TEXT);
                try {
                    sendMessage.setChatId(parseLong(tokens[1]));
                } catch (Exception e) {
                    System.out.println("Expected number");
                    return;
                }
                sendMessage.setText(tokens[2]);
                send(sendMessage);
                break;
            // TODO: implement another types from wiki

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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: написать реализацию. Закройте ресурсы и остановите поток-слушатель
    }

    public static void main(String[] args) throws Exception {

        Client client = null;
        // Пользуемся механизмом контейнера
        try {
            Context context = new Context("client.xml");
            client = (Client) context.getBeanByName("client");
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
