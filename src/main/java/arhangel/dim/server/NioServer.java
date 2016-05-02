package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.store.dao.DaoFactory;
import arhangel.dim.core.store.PostgresDaoFactory;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.nio.ClientPipelineFactory;
import arhangel.dim.session.FixSizedSessionManager;
import arhangel.dim.session.SessionsManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by olegchuikin on 23/04/16.
 */
public class NioServer implements Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;

    private DaoFactory dbFactory;
    private SessionsManager sessionsManager;

    static Logger log = LoggerFactory.getLogger(NioServer.class);

    public static final String address = "127.0.0.1";

    public static final long MAX_CHANNEL_MEMORY_SIZE = 400000000L;
    public static final long MAX_TOTAL_MEMORY_SIZE = 2000000000L;
    public static final long KEEP_ALIVE_TIME = 60;

    @Override
    public DaoFactory getDbFactory() {
        return dbFactory;
    }

    @Override
    public SessionsManager getSessionsManager() {
        return sessionsManager;
    }

    @Override
    public void start() {

        try {
            dbFactory = new PostgresDaoFactory();
        } catch (PersistException e) {
            e.printStackTrace();
            return;
        }

        sessionsManager = new FixSizedSessionManager(this, maxConnection);

        ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(1,
                MAX_CHANNEL_MEMORY_SIZE, MAX_TOTAL_MEMORY_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS);

        ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(4 /* число рабочих потоков */,
                MAX_CHANNEL_MEMORY_SIZE, MAX_TOTAL_MEMORY_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS);

        ServerBootstrap networkServer = new ServerBootstrap(
                new NioServerSocketChannelFactory(bossExec, ioExec, 4 /* то же самое число рабочих потоков */));

        networkServer.setOption("backlog", 500);
        networkServer.setOption("connectTimeoutMillis", 10000);
        networkServer.setPipelineFactory(new ClientPipelineFactory(this));

        Channel channel = networkServer.bind(new InetSocketAddress(address, port));
        log.info("Nio SyncServer started");
        log.info("Sesseions limit: " + maxConnection);
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }


    public void setDbFactory(DaoFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public void setSessionsManager(SessionsManager sessionsManager) {
        this.sessionsManager = sessionsManager;
    }

    public static void main(String[] args) {

        NioServer server = null;
        try {
            Container context = new Container("server.xml");
            server = (NioServer) context.getByName("server");
            log.info("NioServer settings read");
        } catch (InvalidConfigurationException | IllegalAccessException |
                InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            log.error("Failed to create server");
            return;
        }

        server.start();
    }
}
