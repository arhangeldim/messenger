package arhangel.dim.core.messages;

import java.util.Set;

public class ChatListResultMessage extends Message {
    private Set<Long> chatIds;

    public Set<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(Set<Long> chatIds) {
        this.chatIds = chatIds;
    }

    @Override
    public String toString() {
        return "ChatListResultMessage{" +
                "chatIds=" + chatIds +
                "} " + super.toString();
    }
}
