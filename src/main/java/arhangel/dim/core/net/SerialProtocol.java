package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class SerialProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Message msg = null;
        try {
            in = new ObjectInputStream(inputStream);
            msg = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new ProtocolException(e);
        }
        return msg;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(msg);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProtocolException(e);
        }
        return bytes;
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            // who care
        }
        return null;
    }
}
