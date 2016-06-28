package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bais)) {
            Message msg = (Message) in.readObject();
            return msg;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(baos)) {
            out.writeObject(msg);
            byte[] objBytes = baos.toByteArray();
            return objBytes;
        } catch (Exception e) {
            e.getStackTrace();
        }

        return null;
    }
}
