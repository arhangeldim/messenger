package arhangel.dim.lections.socket.nio;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
class EchoWorker implements Runnable {
    private final List<ServerDataEvent> queue = new LinkedList<>();

    void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);
        synchronized (queue) {
            queue.add(new ServerDataEvent(server, socket, dataCopy));
            queue.notify();
        }
    }

    public void run() {
        ServerDataEvent dataEvent;
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Recieved = " + new String(queue.get(0).data));
                dataEvent = queue.remove(0);
            }
            dataEvent.server.send(dataEvent.socket, dataEvent.data);
        }
    }
}
