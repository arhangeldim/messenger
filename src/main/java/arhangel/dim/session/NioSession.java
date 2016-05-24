package arhangel.dim.session;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.server.Server;
import org.jboss.netty.channel.Channel;

import java.io.IOException;

/**
 * Created by olegchuikin on 29/04/16.
 */
public class NioSession extends Session {

    public NioSession(Channel channel, Server server) {
        super(channel, server);
    }

    @Override
    public synchronized void send(Message msg) throws ProtocolException, IOException {
        channel.write(msg);
    }
}
