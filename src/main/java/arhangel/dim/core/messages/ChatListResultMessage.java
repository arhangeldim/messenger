package arhangel.dim.core.messages;

import java.util.List;

public class ChatListResultMessage extends Message {
    private List<Long> chatIds;

    public List<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
    }

    @Override
    public String toString() {
        return "ChatListResultMessage{" +
                "chatIds=" + chatIds +
                "} " + super.toString();
    }
}
