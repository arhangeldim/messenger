package arhangel.dim.client;

import arhangel.dim.core.message.Message;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;

import java.io.DataOutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Класс, отправляющий сообщения серверу
 */

public class MessageWriter implements Runnable {

    private DataOutputStream out;
    private BlockingQueue<Message> messagesToWrite;

    private Protocol<Message> writeProtocol;

    public MessageWriter(DataOutputStream out, BlockingQueue<Message> messagesToWrite) {
        this.out = out;
        this.messagesToWrite = messagesToWrite;
        writeProtocol = new SerializationProtocol<>();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message messageToWrite = messagesToWrite.take();
                out.write(writeProtocol.encode(messageToWrite));
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("MessageWriter: exception caught " + e.toString());
        }
    }
}
