package arhangel.dim.server;

import arhangel.dim.container.Context;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */

public class Server {

    static Logger log = LoggerFactory.getLogger(Server.class);
    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private UserStore userStore;
    private MessageStore messageStore;
    private int bufferSize = 256 * 32;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    private ExecutorService service;
    private List<Session> sessions;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel serverSocketChannel;


    public int getBufferSize() {
        return bufferSize;
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public AsynchronousChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }


    public List<Session> getSessions() {
        return sessions;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public void init() throws Exception {
        service = Executors.newFixedThreadPool(maxConnection);
        sessions = new ArrayList<>();

        channelGroup = AsynchronousChannelGroup.withThreadPool(service);
        serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.bind(new InetSocketAddress(port));

        userStore.init();
        messageStore.init();
    }

    public void run() throws IOException {
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(this, serverSocketChannel);
        serverSocketChannel.accept(null, acceptCompletionHandler);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.info("[run] Main thread interrupted");
        }
    }

    public static void main(String[] args) {

        Server server = null;
        // Пользуемся механизмом контейнера
        try {
            Context context = new Context("server.xml");
            server = (Server) context.getBeanByName("server");
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server: configuration error", e);
            return;
        }
        try {

            server.init();
            server.run();
            /*
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
            }*/
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
