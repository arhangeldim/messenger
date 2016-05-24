package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;
import java.io.*;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try (ObjectInputStream o = new ObjectInputStream(b)){
                Message msg = (Message)o.readObject();
                return msg;
            }
            catch (ClassNotFoundException ex) {
                throw new ProtocolException(ex.getMessage());
            }
        }
        catch (IOException ex) {
            throw new ProtocolException(ex.getMessage());
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(msg);
            }
            catch (IOException ex) {
                throw new ProtocolException(ex.getMessage());
            }
            return b.toByteArray();
        }
        catch (IOException ex) {
            throw new ProtocolException(ex.getMessage());
        }
    }
}
