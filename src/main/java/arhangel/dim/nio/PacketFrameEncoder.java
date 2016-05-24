package arhangel.dim.nio;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Protocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by olegchuikin on 24/04/16.
 */
public class PacketFrameEncoder extends OneToOneEncoder {

    static Logger log = LoggerFactory.getLogger(PacketFrameEncoder.class);

    Protocol protocol;

    private Long pipeLineId;

    public PacketFrameEncoder(Protocol protocol, Long pipeLineId) {
        this.protocol = protocol;
        this.pipeLineId = pipeLineId;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            return msg;
        }

        Message message = (Message) msg;

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(protocol.encode(message));

        log.info("Message encoded by pipeline " + pipeLineId + ": " + msg);
        return buffer;
    }
}
