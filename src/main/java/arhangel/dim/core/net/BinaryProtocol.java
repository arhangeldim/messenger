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

public class BinaryProtocol implements Protocol {

    static Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            return (Message) object;
        } catch (Exception e) {
            throw new ProtocolException(e);
        } finally {
            try {
                bis.close();
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.info("IOException");
            }
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new ProtocolException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                bos.close();
            } catch (IOException ex) {
                log.info("IOException");
            }
        }
    }
}
