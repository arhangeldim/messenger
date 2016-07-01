package arhangel.dim.core.messages;

import java.util.Objects;
import java.util.stream.Collectors;

public class ListChatMessage extends Message {
    String chatsList;

    public ListChatMessage() {
        this.setType(Type.MSG_CHAT_LIST);
    }

    public void setChatsList(String chatsList) {
        this.chatsList = chatsList;
    }

    public String getChatsList() {
        return chatsList;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ListChatMessage listChatMessage = (ListChatMessage) other;

        return Objects.equals(chatsList, listChatMessage.chatsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChatsList());
    }

    @Override
    public String toString() {
        return "Chat list: " +
                chatsList;
    }
}
