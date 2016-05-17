package arhangel.dim.container.beans;

/**
 * Created by Дмитрий on 16.05.2016.
 */
public class WebConnection {
    private int port;
    private String host;

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHost(int host) {
        if (host == 0) {
            this.host = "localhost";
        }
    }

    public WebConnection() {
        port = 9000;
        host = "localhost";
    }

    public WebConnection(String host, int port) {
        this.port = port;
        this.host = host;
    }
}
