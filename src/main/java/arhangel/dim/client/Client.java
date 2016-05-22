package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import arhangel.dim.core.messages.CreateChatMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.ListChatMessage;
import arhangel.dim.core.messages.ListChatResultMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.server.Server;
import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.exceptions.InvalidConfigurationException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
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

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;
    private Socket socket;
    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    private Long userId;

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
        System.out.println(host);
        System.out.println(port);
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
                        log.info("Decoded message:" + msg);
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
                StatusMessage msgStatus = (StatusMessage) msg;
                System.out.println(msgStatus.getStatus());
                break;
            case MSG_CHAT_LIST_RESULT:
                ListChatResultMessage msgChatListResult = (ListChatResultMessage) msg;
                if (msgChatListResult.getChatIds().size() == 0) {
                    System.out.println("You have no chats yet.");
                } else {
                    System.out.println("Your chats: " + String.join(",", msgChatListResult.getChatIds().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList())));
                }
                break;
            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                StringBuilder sb = new StringBuilder();
                sb.append("Info. User: ").append(infoMessage.getUserId())
                        .append(".");
                System.out.println(sb.toString());
                break;
            case MSG_TEXT:
                TextMessage textMessage = (TextMessage) msg;
                System.out.println(msg);
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
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (tokens.length != 3) {
                    log.error("Invalid args number");
                    break;
                }
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setLogin(tokens[1]);
                loginMessage.setPassword(tokens[2]);
                send(loginMessage);
                break;
            case "/help":
                // TODO: реализация
                break;
            case "/text":
                if (tokens.length != 3) {
                    log.error("Invalid args number");
                    break;
                }
                TextMessage sendMessage = new TextMessage();
                sendMessage.setChatId(Long.parseLong(tokens[1]));
                sendMessage.setText(tokens[2]);
                send(sendMessage);
                break;
            case "/info":
                InfoMessage infoMessage = new InfoMessage();
                if (tokens.length == 1) {
                    infoMessage.setArg(false);
                } else {
                    infoMessage.setArg(true);
                    infoMessage.setUserId(Long.parseLong(tokens[1]));
                }
                send(infoMessage);
                break;
            case "/chat_create":
                CreateChatMessage createChatMessage = new CreateChatMessage();
                String[] userIdsStr = tokens[1].split(",");
                List<Long> userIds = new ArrayList<Long>();
                for (int i = 0; i < userIdsStr.length; ++i) {
                    userIds.add(Long.parseLong(userIdsStr[i]));
                }
                createChatMessage.setUsersIds(userIds);
                send(createChatMessage);
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
        log.info("send to server: " + msg.toString());
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void close() throws IOException {
        log.error("Closing socket...");
        if (!socketThread.isInterrupted()) {
            socketThread.interrupt();
        }
        if (!socket.isClosed()) {
            socket.close();
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
                if ("exit".equals(input)) {
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
