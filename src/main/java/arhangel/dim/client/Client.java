package arhangel.dim.client;

import arhangel.dim.client.commands.ChatCreateCommand;
import arhangel.dim.client.commands.ChatHistoryCommand;
import arhangel.dim.client.commands.ChatListCommand;
import arhangel.dim.client.commands.InfoCommand;
import arhangel.dim.client.commands.LoginCommand;
import arhangel.dim.client.commands.TextCommand;
import arhangel.dim.client.commands.UserCreateCommand;
import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Клиент для тестирования серверного приложения
 */
public class Client implements ConnectionHandler {

    /**
     * Механизм логирования позволяет более гибко управлять записью данных в лог (консоль, файл и тд)
     * */
    private static Logger log = LoggerFactory.getLogger(Client.class);

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

    /**
     * Текущий пользователь
     */
    private ClientUser user;

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

    private void initSocket() throws IOException {
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
        if (checkUserStatus(msg)) {
            System.out.println("Server: " + msg.toString());
        }
    }

    private boolean checkUserStatus(Message msg) {
        if (msg.getType() == Type.MSG_STATUS) {
            StatusMessage status = (StatusMessage) msg;
            if (status.getUsername() != null) {
                user.login(status.getId(), status.getUsername());
                log.info("Logged in as ({}){}", user.getId(), user.getName());
            }
        }

        if (msg.getId() != null) {
            if (user.isLoginnedFlag()) {
                if (msg.getId().equals(user.getId())) {
                    log.error("Wrong receiver");
                    return false;
                }
            }
        }
        return true;
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

        Client client;
        // Пользуемся механизмом контейнера
        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create client", e);
            return;
        }

        log.debug("Client created");

        ClientMessageCreator commandlineHandler = new ClientMessageCreator()
                .addHandler(new ChatCreateCommand("/chat_create"))
                .addHandler(new ChatHistoryCommand("/chat_history"))
                .addHandler(new ChatListCommand("/chat_list"))
                .addHandler(new InfoCommand("/info"))
                .addHandler(new LoginCommand("/login"))
                .addHandler(new TextCommand("/text"))
                .addHandler(new UserCreateCommand("/user_create"));

        log.debug("commandLineHandler created");

        try {
            client.initSocket();
            client.user = new ClientUser();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$");
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    Message message = commandlineHandler.handleCommandline(input, client.user);
                    if (message != null) {
                        client.send(message);
                    }
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
