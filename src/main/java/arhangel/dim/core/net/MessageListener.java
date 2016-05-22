package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

/**
 * Created by tatiana on 19.04.16.
 */
public interface MessageListener {
    void onMessage(Message message, long id);
}
