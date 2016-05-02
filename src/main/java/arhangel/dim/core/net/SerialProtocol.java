package arhangel.dim.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.core.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;



/**
 * Простейший протокол передачи данных
 */
public class SerialProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        //Message msg = (Message) bis.readObject();
        ObjectInput in = null;
        Message msg = null;
        try {
            in = new ObjectInputStream(bis);
            msg = (Message) in.readObject();
        } catch (IOException ex) {
            throw new ProtocolException("IOException while reading bytes");
        } catch (ClassNotFoundException ex) {
            throw new ProtocolException("ClassNotFoundException while deserializing bytes");
        }
        return msg;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            yourBytes = bos.toByteArray();
        } catch (IOException ex) {
            throw new ProtocolException("IOException while serializing message");
        }
        return yourBytes;
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
