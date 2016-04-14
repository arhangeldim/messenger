package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;


import java.io.*;
import java.nio.ByteBuffer;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bais);

            Message msg = (Message) in.readObject();
            return msg;

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);

            out.writeObject(msg);
            byte[] objBytes = baos.toByteArray();
            baos.flush();
            baos.close();
            out.flush();
            out.close();

            return objBytes;

        } catch (IOException e) {
            e.getMessage();
        }

        return null;
    }
}
