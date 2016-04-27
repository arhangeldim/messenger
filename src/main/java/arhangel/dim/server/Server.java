package arhangel.dim.server;

import arhangel.dim.core.net.Protocol;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private String dbLoc;
    private String dbLogin;
    private String dbPassword;

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }

    public String getDbLoc() {
        return this.dbLoc;
    }

    public String getDbLogin() {
        return  this.dbLogin;
    }

    public String getDbPassword() {
        return  this.dbPassword;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
