package arhangel.dim.nio;

import arhangel.dim.core.messages.ErrorMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.session.AddSessionToManagerException;
import arhangel.dim.session.NioSession;
import arhangel.dim.session.Session;
import arhangel.dim.server.NioServer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by olegchuikin on 24/04/16.
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private NioServer server;
    private PacketFrameDecoder decoder;
    private PacketFrameEncoder encoder;

    private Session session;
    private Channel channel;

    public ClientHandler(PacketFrameDecoder decoder, PacketFrameEncoder encoder, NioServer server) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.server = server;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        log.info("channel connected");

        this.channel = e.getChannel();
        session = new NioSession(e.getChannel(), server);
        try {
            server.getSessionsManager().addSession(session);
        } catch (AddSessionToManagerException exception){
            log.info("Close channel: " + exception.getMessage());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setText(exception.getMessage());
            session.send(errorMessage);
            channel.close();
        }

    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        server.getSessionsManager().removeSession(session);
        log.info("channel disconnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
        log.info("channel closed");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);


        if (e.getChannel().isOpen()) {
            Message message = (Message) e.getMessage();
            log.info("Message receved", message);
            session.onMessage(message);

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
        ctx.getChannel().close();

        log.info("exception caught");

    }
}
