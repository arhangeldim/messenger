package arhangel.dim.nio;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Protocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by olegchuikin on 24/04/16.
 */
public class PacketFrameDecoder extends ReplayingDecoder<VoidEnum> {

    static Logger log = LoggerFactory.getLogger(PacketFrameDecoder.class);

    Protocol protocol;

    private Long pipeLineId;

    public PacketFrameDecoder(Protocol protocol, Long pipeLineId) {
        this.protocol = protocol;
        this.pipeLineId = pipeLineId;
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        ctx.sendUpstream(event);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        ctx.sendUpstream(event);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        int len = buffer.readableBytes();
        if (!(len > 0)) {
            return null;
        }
        int size = buffer.readInt();
        byte[] buf = new byte[size];
        buffer.readBytes(buf);
        Message msg = protocol.decode(buf);
        log.info("Message decoded by pipeLine " + pipeLineId + ": " + msg);

        return msg;
    }
}
