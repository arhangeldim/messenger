package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class BinaryProtocolTest {

    @Test
    public void decode() throws Exception {
        Protocol protocol = new BinaryProtocol();
        TextMessage message = new TextMessage(1L, "Hello");

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