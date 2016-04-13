package arhangel.dim.core.net;

import org.junit.Assert;
import org.junit.Test;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;

/**
 *
 */
public class BinaryProtocolTest {

    @Test
    public void decode() throws Exception {
        Protocol protocol = new BinaryProtocol();
        TextMessage message = new TextMessage();
        message.setText("Hello");
        message.setSenderId(1L);
        message.setType(Type.MSG_TEXT);

        byte[] data = protocol.encode(message);
        Assert.assertTrue(data != null);

        Message other = protocol.decode(data);
        Assert.assertEquals(message, other);

    }

    @Test(expected = ProtocolException.class)
    public void testException() throws ProtocolException {
        Protocol protocol = new BinaryProtocol();
        byte[] failureData = {6, 3, 10};
        Message msg = protocol.decode(failureData);
    }
}