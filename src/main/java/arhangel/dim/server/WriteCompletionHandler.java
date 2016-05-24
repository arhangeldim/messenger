package arhangel.dim.server;

import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, Session> {
    private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);

    public WriteCompletionHandler(Server server) {
        this.server = server;
    }

    private Server server;

    @Override
    public void completed(Integer bytesWritten, Session session) {
        if (bytesWritten < 1) {
            log.info("[completed] Closing session to {}", session.getUser());
            synchronized (server.getSessions()) {
                server.getSessions().remove(session);
                session.close();
            }
            return;
        }
        log.info("[completed] Wrote {} bytes to {}", bytesWritten, session.getUser());
    }

    @Override
    public void failed(Throwable exc, Session session) {
        log.error("[failed] Failed to write, closing session", exc);
        synchronized (server.getSessions()) {
            server.getSessions().remove(session);
            session.close();
        }
    }
}
