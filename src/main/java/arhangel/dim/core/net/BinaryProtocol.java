package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryProtocol implements Protocol {
    private static Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        Message message = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            message = (Message) objectInputStream.readObject();
            log.info("[decode] Decoded message {}", message);
        } catch (ClassNotFoundException e) {
            log.error("[decode] Error while decoding message - class not found", e);
        } catch (IOException e) {
            log.error("[decode] Error while decoding message - IO exception", e);
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return message;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(msg);
            bytes = byteArrayOutputStream.toByteArray();
            log.info("[encode] Encoded message {} into {} bytes", msg, bytes.length);
        } catch (IOException e) {
            log.error("[encode] Error while encoding message", e);
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }
}
