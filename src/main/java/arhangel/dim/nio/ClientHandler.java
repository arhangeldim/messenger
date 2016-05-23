package arhangel.dim.nio;

import arhangel.dim.core.messages.ErrorMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.session.AddSessionToManagerException;
import arhangel.dim.session.NioSession;
import arhangel.dim.session.Session;
import arhangel.dim.server.NioServer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
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
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        super.channelConnected(ctx, event);
        log.info("channel connected");

        this.channel = event.getChannel();
        session = new NioSession(event.getChannel(), server);
        try {
            server.getSessionsManager().addSession(session);
        } catch (AddSessionToManagerException exception) {
            log.info("Close channel: " + exception.getMessage());
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setText(exception.getMessage());
            session.send(errorMessage);
            channel.close();
        }

    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        super.channelDisconnected(ctx, event);
        server.getSessionsManager().removeSession(session);
        log.info("channel disconnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        super.channelClosed(ctx, event);
        log.info("channel closed");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
        super.messageReceived(ctx, event);


        if (event.getChannel().isOpen()) {
            Message message = (Message) event.getMessage();
            log.info("Message receved", message);
            session.onMessage(message);

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) throws Exception {
        super.exceptionCaught(ctx, event);
        ctx.getChannel().close();

        log.info("exception caught");

    }
}
