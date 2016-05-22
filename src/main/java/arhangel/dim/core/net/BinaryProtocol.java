package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

import java.awt.color.ProfileDataException;
import java.io.*;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return (Message) o;
        }catch (Exception e){
            throw new ProtocolException(e);
        } finally {
            try {
                bis.close();
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
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
            return  bos.toByteArray();
        } catch (Exception e){
            throw new ProtocolException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                bos.close();
            } catch (IOException ex) {
            }
        }
    }
}
