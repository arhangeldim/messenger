package arhangel.dim.core.messages;

/**
 * Created by d_k on 19.04.16.
 */
public class ChatListMessage extends Message {
    public ChatListMessage() {
        super();
        this.setType(Type.MSG_CHAT_LIST);
    }
}
