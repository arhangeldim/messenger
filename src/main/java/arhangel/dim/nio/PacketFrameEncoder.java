package arhangel.dim.nio;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.StringProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Created by olegchuikin on 24/04/16.
 */
public class PacketFrameEncoder extends OneToOneEncoder{

    Protocol protocol;

    public PacketFrameEncoder(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            return msg;
        }

        Message message = (Message) msg;

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(protocol.encode(message));
        return buffer;
    }
}
