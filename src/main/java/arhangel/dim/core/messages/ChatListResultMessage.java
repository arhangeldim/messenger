package arhangel.dim.core.messages;

import java.util.List;

public class ChatListResultMessage extends Message {
    private List<Long> chats;

    ChatListResultMessage() {
        super();
        this.setType(Type.MSG_CHAT_LIST_RESULT);
    }

    public List<Long> getChats() {
        return chats;
    }

    public void setChats(List<Long> chats) {
        this.chats = chats;
    }
}
