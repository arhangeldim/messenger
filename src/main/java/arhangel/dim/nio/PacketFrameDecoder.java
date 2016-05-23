package arhangel.dim.nio;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.StringProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by olegchuikin on 24/04/16.
 */
public class PacketFrameDecoder extends ReplayingDecoder<VoidEnum> {

    static Logger log = LoggerFactory.getLogger(PacketFrameDecoder.class);

    Protocol protocol;

    public PacketFrameDecoder(Protocol protocol) {
        this.protocol = protocol;
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
//            throw new IOException();
            return null;
        }
        int size = buffer.readInt();
        byte[] buf = new byte[size];
        buffer.readBytes(buf);
        log.info("Input message readed", new String(buf));
        Message msg = protocol.decode(buf);
        log.info("Message received: " + msg);

        return msg;
    }
}
