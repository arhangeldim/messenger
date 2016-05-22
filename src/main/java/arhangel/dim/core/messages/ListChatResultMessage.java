package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by tatiana on 28.04.16.
 */
public class ListChatResultMessage extends Message {

    public ListChatResultMessage() {
        this.setType(Type.MSG_CHAT_LIST_RESULT);
    }

    private List<Long> chatsIds = new ArrayList<>();

    public List<Long> getChatIds() {
        return chatsIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatsIds = chatIds;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ListChatResultMessage listChatResultMessage = (ListChatResultMessage) object;

        return Objects.equals(chatsIds, listChatResultMessage.chatsIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatsIds);
    }

    @Override
    public String toString() {
        return "Chat list: " +
                String.join(",", chatsIds.stream().map(Object::toString).collect(Collectors.toList()));
    }
}
