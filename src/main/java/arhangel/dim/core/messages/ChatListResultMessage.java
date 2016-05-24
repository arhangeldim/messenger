package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static arhangel.dim.core.messages.Type.MSG_CHAT_LIST_RESULT;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ChatListResultMessage extends Message {

    private List<Long> chatIds = new ArrayList<>();

    public ChatListResultMessage() {
        super(MSG_CHAT_LIST_RESULT);
    }

    public List<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ChatListResultMessage that = (ChatListResultMessage) object;

        return !(chatIds != null ? !chatIds.equals(that.chatIds) : that.chatIds != null);

    }

    @Override
    public int hashCode() {
        return chatIds != null ? chatIds.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChatListResultMessage{" +
                "chats='" +
                String.join(",", chatIds.stream().map(Object::toString).collect(Collectors.toList())) + '\'' +
                '}';
    }
}
