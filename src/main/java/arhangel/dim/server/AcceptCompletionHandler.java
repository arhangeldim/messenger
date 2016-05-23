package arhangel.dim.server;

import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
    static Logger log = LoggerFactory.getLogger(AcceptCompletionHandler.class);

    private Server server;
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AcceptCompletionHandler(Server server, AsynchronousServerSocketChannel asynchronousServerSocketChannel) {
        this.server = server;
        this.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel result, Void attachment) {
        try {
            log.info("[completed] Accepted connection from {}", result.getRemoteAddress().toString());
        } catch (IOException e) {
            log.error("[completed] Accepted connection from somebody, but couldn't get remote address", e);
        }
        asynchronousServerSocketChannel.accept(null, this);

        Session session = new Session(server);
        session.setUser(null);
        session.setAsynchronousSocketChannel(result);
        synchronized (server.getSessions()) {
            server.getSessions().add(session);
        }

        ByteBuffer inputBuffer = ByteBuffer.allocate(server.getBufferSize());
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, inputBuffer, server);
        result.read(inputBuffer, session, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        log.error("[failed] Failed to accept connection", exc);
        asynchronousServerSocketChannel.accept(null, this);
    }
}
