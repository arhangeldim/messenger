package arhangel.dim.core.messages;

import java.util.List;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatListResultMessage extends Message {
    public List<Long> getChats() {
        return chats;
    }

    public void setChats(List<Long> chats) {
        this.chats = chats;
    }

    private List<Long> chats;

    public ChatListResultMessage() {
        setType(Type.MSG_CHAT_LIST_RESULT);
    }
}
