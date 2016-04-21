package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Message;
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
        log.info("Message received: {}", msg.toString());
        if (msg.getType() == Type.MSG_LOGIN) {
            Scanner scanner = new Scanner(System.in);
            log.info("Choose login");
            String login = scanner.nextLine();
            log.info("Choose password");
            String password = scanner.nextLine();
            log.info("Register user with login '" + login + "' and password '" + password + "'?[Y/N]");
            String answer = scanner.nextLine();
            if ("Y".equals(answer)) {
                LoginMessage loginMsg = new LoginMessage();
                loginMsg.setLogin(login);
                loginMsg.setPassword(password);
                loginMsg.setType(Type.MSG_REGISTER);
                try {
                    send(loginMsg);
                } catch (ProtocolException e) {
                    log.error("ProtocolException, failed to send registry message");
                } catch (IOException e) {
                    log.error("IOException, failed to send registry message");
                }
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
                LoginMessage loginMsg = new LoginMessage();
                if (tokens.length == 3) {
                    loginMsg.setLogin(tokens[1]);
                    loginMsg.setPassword(tokens[2]);
                } else if (tokens.length != 1) {
                    log.error("Wrong format of login request. Type /help to see the right format");
                    break;
                }
                loginMsg.setType(Type.MSG_LOGIN);
                send(loginMsg);
                break;
            case "/help":
                log.info("SOME HELP INFO.");
                break;
            case "/text":
                // FIXME: пример реализации для простого текстового сообщения
                if (tokens.length == 3) {
                    TextMessage sendMessage = new TextMessage();
                    sendMessage.setType(Type.MSG_TEXT);
                    sendMessage.setChatId(Long.valueOf(tokens[1]).longValue());
                    sendMessage.setText(tokens[2]);
                    send(sendMessage);
                } else {
                    log.error("Wrong format of text message. Type /help to see the right format");
                }
                break;
            case "/chat_create":
                if (tokens.length == 2) {
                    ChatCreateMessage crtMsg = new ChatCreateMessage();
                    crtMsg.setUserList(tokens[1].split(","));
                    crtMsg.setType(Type.MSG_CHAT_CREATE);
                    send(crtMsg);
                } else {
                    log.error("Wrong format of chat creation request. Type /help to see the right format");
                }
                break;
            case "/chat_history":
                if (tokens.length == 2) {
                    ChatHistoryMessage histMsg = new ChatHistoryMessage();
                    histMsg.setChatId(Long.valueOf(tokens[1]));
                    histMsg.setType(Type.MSG_CHAT_HIST);
                    send(histMsg);
                } else {
                    log.error("Wrong format of chat history request. Type /help to see the right format");
                }
                break;
            case "/chat_list":
                ChatListMessage listMsg = new ChatListMessage();
                listMsg.setType(Type.MSG_CHAT_LIST);
                send(listMsg);
                break;
            case "/info":
                StatusMessage st = new StatusMessage();
                st.setType(Type.MSG_INFO);
                if (tokens.length == 1) {
                    st.setText("self");
                } else {
                    st.setText(tokens[1]);
                }
                send(st);
                // TODO: implement another types from wiki
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
        log.info("Sending message: " + msg.toString());
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
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
            System.out.println(client.port);
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
