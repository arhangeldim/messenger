package arhangel.dim.nio;

import arhangel.dim.server.NioServer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by olegchuikin on 23/04/16.
 */

public class ClientPipelineFactory implements ChannelPipelineFactory {

    static Logger log = LoggerFactory.getLogger(ClientPipelineFactory.class);

    private NioServer server;

    private AtomicLong pipeLineId = new AtomicLong(0L);

    public ClientPipelineFactory(NioServer server) {
        this.server = server;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        pipeLineId.getAndIncrement();
        log.info("Create new Pipeline with ID: " + pipeLineId);
        PacketFrameDecoder decoder = new PacketFrameDecoder(server.getProtocol(), pipeLineId.get());
        PacketFrameEncoder encoder = new PacketFrameEncoder(server.getProtocol(), pipeLineId.get());
        return Channels.pipeline(decoder, encoder, new ClientHandler(server, pipeLineId.get()));
    }
}
