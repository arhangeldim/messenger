package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

public interface MessageListener {
    void onMessage(Message message, long id);
}
