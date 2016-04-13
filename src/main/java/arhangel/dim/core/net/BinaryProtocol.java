package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    private class ChecksumMessage {
        int size;
        Message message;

        ChecksumMessage(int size, Message message) {
            this.size = size;
            this.message = message;
        }
    }

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int size = buffer.getInt();
            if (bytes.length != size + 4) {
                throw new ProtocolException("Size not match");
            }
            byte[] messageBytes = new byte[size];
            buffer.get(messageBytes);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(messageBytes))) {
                return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new ProtocolException(e);
            }
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            oos.flush();

            byte[] messageBytes = baos.toByteArray();
            int size = messageBytes.length;

            ByteBuffer buffer = ByteBuffer.allocate(size + 4);
            buffer.putInt(size);
            buffer.put(messageBytes);

            return buffer.array();
        } catch (IOException e) {
            throw new ProtocolException(e);
        }

    }

}
