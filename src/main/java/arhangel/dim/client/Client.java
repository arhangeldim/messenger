package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import arhangel.dim.core.messages.*;
import arhangel.dim.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

import static arhangel.dim.core.messages.Type.*;

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
                sb.append("Info. User: ").append(infoResultMessage.getName())
                        .append(", chats: ").append(String.join(",", infoResultMessage.getChats()
                        .stream()
                        .map(Object::toString).collect(Collectors.toList())))
                        .append(".");
                System.out.println(sb.toString());
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
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setType(MSG_LOGIN);
                loginMessage.setLogin(tokens[1]);
                loginMessage.setPassword(tokens[2]);
                send(loginMessage);
                break;
            case "/chat_list":
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setType(MSG_CHAT_LIST);
                send(chatListMessage);
                break;
            case "/chat_create":
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setUserIds(ParseUtils
                        .stringArrToLongList(tokens[1].replaceAll("[\\s]+", "").split(",")));
                send(chatCreateMessage);
                break;
            case "/help":
                System.out.println(helpInfo());
                break;
            case "/text":
                // FIXME: пример реализации для простого текстового сообщения
                TextMessage sendMessage = new TextMessage();
                sendMessage.setType(MSG_TEXT);
                sendMessage.setChatId(Long.parseLong(tokens[1]));
                sendMessage.setText(line.replace(tokens[0] + " " + tokens[1] + " ", ""));
                send(sendMessage);
                break;
            case "/info":
                InfoMessage infoMessage = new InfoMessage();
                infoMessage.setType(MSG_INFO);
                if (tokens.length == 1){
                    infoMessage.setTarget(-1L);
                } else {
                    infoMessage.setTarget(Long.parseLong(tokens[1]));
                }
                send(infoMessage);
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
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        socketThread.interrupt();
        // TODO: написать реализацию. Закройте ресурсы и остановите поток-слушатель
    }

    private String helpInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: /command [arg...]").append("\n");
        sb.append("Available commands:").append("\n");
        sb.append("\t/login <login> <password>").append("\n");
        sb.append("\t/chat_list").append("\n");
        sb.append("\t/chat_create <user_id...>").append("\n");
        sb.append("\t/text <chat_id> <text>");
        return sb.toString();
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
            while (true) {
                System.out.print("$");
                String input = scanner.nextLine();
                log.info("Command readed from console");
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
