package arhangel.dim.core.messages;

import java.util.List;

public class ChatListResultMessage extends Message {
    private List<Long> chats;

    public List<Long> getChats() {
        return chats;
    }

    public void setChats(List<Long> chats) {
        this.chats = chats;
    }
}
