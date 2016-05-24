package arhangel.dim.lections.socket.nio;

import java.nio.channels.SocketChannel;

/**
 *
 */
class ChangeRequest {
    static final int CHANGEOPS = 2;

    public SocketChannel socket;
    public int type;
    int ops;

    ChangeRequest(SocketChannel socket, int type, int ops) {
        this.socket = socket;
        this.type = type;
        this.ops = ops;
    }
}
