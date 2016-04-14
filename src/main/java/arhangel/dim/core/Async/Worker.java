package arhangel.dim.core.Async;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.server.Server;

/**
 * Created by philip on 14.04.16.
 */
public class Worker implements Runnable {
    private int id;
    Server server;
    byte[] buf;

    public Worker(Server server, byte[] buf) {
        this.server = server;
        this.buf = buf;
    }

    public void run() {
        System.out.println("Worker is here!");
        Message msg = null;
        try {
            msg = server.getProtocol().decode(buf);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        server.commandHandle(msg);
    }
}
