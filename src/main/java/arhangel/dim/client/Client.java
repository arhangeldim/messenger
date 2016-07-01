package arhangel.dim.client;

import arhangel.dim.container.Container;
import arhangel.dim.container.exceptions.InvalidConfigurationException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.ListChatResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.CreateChatMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.StringProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

    public static int CONNECTION_TIMEOUT_SECONDS = 10;

    public void initSocket() throws IOException {
        log.info("Создание сетевого подключения: " + host + ":" + port);
        try {
            Socket socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (ConnectException error) {
            log.error("Невозможно установить подключение, " +
                    "следующая попытка через " + CONNECTION_TIMEOUT_SECONDS + " секунд");
            try {
                Thread.sleep(CONNECTION_TIMEOUT_SECONDS * 1000L);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }

            initSocket();
            return;
        }

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
                        log.info("< New Message received >");
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
    }

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {
        switch (msg.getType()) {
            case MSG_STATUS:
                StatusMessage msgStatus = (StatusMessage) msg;
                log.info(msgStatus.getStatus());
                System.out.println(msgStatus.getStatus());
                break;
            case MSG_CHAT_LIST_RESULT:
                ListChatResultMessage msgChatListResult = (ListChatResultMessage) msg;
                if (msgChatListResult.getChatIds().size() == 0) {
                    log.info("У вас нет активных чатов в данный момент");
                } else {
                    log.info("Ваши активные чаты: " + String.join(",", msgChatListResult.getChatIds().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList())));
                }
                System.out.println("Ваши активные чаты: " + String.join(",", msgChatListResult.getChatIds().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())));
                break;
            case MSG_INFO:
                InfoMessage infoMessage = (InfoMessage) msg;
                log.info(infoMessage.getInfo());
                break;
            case MSG_TEXT:
                log.info(msg.toString());
                System.out.println(msg.toString());
                break;
            default:
                log.error("Данный тип сообщений не поддерживается");
                System.err.println("Полученный тип сообщений не поддерживается");
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
                    log.error("Неправильное использование команды");
                    break;
                }
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setLogin(tokens[1]);
                loginMessage.setPassword(tokens[2]);
                send(loginMessage);
                break;
            case "/help":
                log.info(
                        "login <имя пользователя> <пароль> - выполнить вход\n" +
                                "help - справка по месседжеру\n" +
                                "text <id чата> <сообщение> - отправить сообщение в чат с заданным id\n" +
                                "info <id пользователя> - получить информациию о пользователе\n" +
                                "info - получить информациию о себе\n" +
                                "chat_create <user id list> - создать новый чат " +
                                "(использование: chat_create 1, 2, 3, 66)\n"
                );
                break;
            case "/text":
                if (tokens.length != 3) {
                    log.error("Неправильное использование команды");
                    break;
                }
                TextMessage sendMessage = new TextMessage();
                sendMessage.setChatId(Long.parseLong(tokens[1]));
                sendMessage.setText(tokens[2]);
                send(sendMessage);
                break;
            case "/info":
                InfoMessage infoMessage = new InfoMessage();
                if (tokens.length > 1) {
                    infoMessage.setUserId(Long.parseLong(tokens[1]));
                } else {
                    infoMessage.setUserId(-1);
                }
                infoMessage.setInfo(null);
                send(infoMessage);
                break;
            case "/chat_create":
                CreateChatMessage createChatMessage = new CreateChatMessage();
                String[] userIdsStr = tokens[1].split(",");
                List<Long> userIds = new ArrayList<>();
                for (String anUserIdsStr : userIdsStr) {
                    userIds.add(Long.parseLong(anUserIdsStr));
                }
                createChatMessage.setUsersIds(userIds);
                send(createChatMessage);
                break;
            case "/chat_hist":
                //TODO: Доделать
            case "/chat_list":
                //TODO: Доделать
            default:
                log.error("Команда не найдена: " + line);
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info("< Sending new message >");
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

        Client client;
        // Пользуемся механизмом контейнера
        //TODO: or not o:
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
            //TODO FIX
            client.setProtocol(new StringProtocol());
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
