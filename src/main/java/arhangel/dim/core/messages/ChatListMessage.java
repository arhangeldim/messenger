package arhangel.dim.core.messages;


import static arhangel.dim.core.messages.Type.MSG_CHAT_LIST;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ChatListMessage extends Message {

    public ChatListMessage() {
        super(MSG_CHAT_LIST);
    }

    @Override
    public String toString() {
        return "ChatListMessage{" +
                '}';
    }
}
