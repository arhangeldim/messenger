package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("failed to decode", e);
            throw new ProtocolException("Failed to decode message", e);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("Failed to encode message", e);
        }
    }
}
