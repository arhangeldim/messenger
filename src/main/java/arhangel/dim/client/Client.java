package arhangel.dim.client;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

public class Client implements ConnectionHandler {

    private Long userId;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private Protocol protocol;
    private int port;
    private String host;

    private Thread socketThread;
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

    public InputStream getIn() {
        return in;
    }

    public Thread getSocketThread() {
        return socketThread;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void initSocket() throws IOException {
        socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 500];
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
        switch (msg.getType().toString()) {
            case "MSG_STATUS":
                StatusMessage statusMsg = (StatusMessage) msg;
                if (statusMsg.getStatus().equals("Logged in")) {
                    this.setUserId(statusMsg.getSenderId());
                    log.info("You logged in as user with id = " + statusMsg.getSenderId().toString());
                }
                break;
            default: log.error("Unknown recieved message");
        }
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
                // FIXME: на тестах может вызвать ошибку
                if (tokens.length < 3) {
                    log.error("Not enough arguments for login");
                    return false;
                } else if (tokens.length > 3) {
                    log.error("Too many arguments for login");
                    return false;
                }
                LoginMessage msg = new LoginMessage();
                msg.setType(Type.MSG_LOGIN);
                msg.setLogin(tokens[1]);
                msg.setPassword(tokens[2]);

                send(msg);
                return true;
            case "/text":
                if (tokens.length < 3) {
                    log.error("Not enough arguments for message");
                    return false;
                } else if (tokens.length > 3) {
                    log.error("Too many arguments for message");
                    return false;
                }
                TextMessage textMessage = new TextMessage();
                if (this.getUserId() == null) {
                    log.error("Can't send a message while not logged in");
                    return false;
                }
                textMessage.setSenderId(this.getUserId());
                textMessage.setType(Type.MSG_TEXT);
                textMessage.setChatId(Long.parseLong(tokens[1]));
                textMessage.setText(tokens[2]);

                send(textMessage);
                return true;
            case "/help":
                // TODO: Что-то ещё в help?
                System.out.println("Messenger v1.0");
                return true;
            case "/info":
                InfoMessage infomsg = new InfoMessage();
                infomsg.setType(Type.MSG_INFO);

                // TODO: Случай самоинформации
                if (tokens[1].isEmpty()) {
                    log.debug("Self-information case");
                } else {
                    infomsg.setId(Long.getLong(tokens[1]));
                }
                send(infomsg);
                return true;
            default:
                log.error("Unknown input command: " + line);
                return false;
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
    public void close() throws IOException {
        if ( !getSocket().isClosed()) {
            getSocket().close();
        }
        if (!getSocketThread().isInterrupted()) {
            getSocketThread().interrupt();
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
                    if (!client.processInput(input)) {
                        continue;
                    }
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }

                byte[] buf = new byte[1024 * 500];
                int readBytes = client.getIn().read(buf);
                Message msg = client.getProtocol().decode(buf);

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
