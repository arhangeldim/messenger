package arhangel.dim.server;

import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, Session> {
    static Logger log = LoggerFactory.getLogger(ReadCompletionHandler.class);

    private AsynchronousSocketChannel asynchronousSocketChannel;
    private ByteBuffer inputBuffer;
    private Server server;

    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel,
                                 ByteBuffer inputBuffer,
                                 Server server) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.inputBuffer = inputBuffer;
        this.server = server;
    }

    @Override
    public void completed(Integer bytesRead, Session session) {
        if (bytesRead < 1) {
            log.info("[completed] Closing session to {}", session.getUser());
            synchronized (server.getSessions()) {
                server.getSessions().remove(session);
                session.close();
            }
            return;
        }
        byte[] buffer = new byte[bytesRead];
        inputBuffer.flip();
        inputBuffer.get(buffer);
        inputBuffer.flip();
        inputBuffer.clear();
        log.info("[completed] Read {} bytes from {}", bytesRead, session.getUser());

        try {
            session.onMessage(server.getProtocol().decode(buffer));
        } catch (ProtocolException e) {
            log.error("[completed] Couldn't decode received data", e);
        }

        asynchronousSocketChannel.read(inputBuffer, session, this);
    }

    @Override
    public void failed(Throwable exc, Session session) {
        log.error("[failed] Failed to read, closing session", exc);
        synchronized (server.getSessions()) {
            server.getSessions().remove(session);
            session.close();
        }
    }
}
