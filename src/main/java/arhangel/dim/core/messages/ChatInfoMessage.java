package arhangel.dim.core.messages;

import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 23/05/16.
 */
public class ChatInfoMessage extends Message {

    public ChatInfoMessage() {
        super(Type.MSG_CHAT_INFO);
    }

    private Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ChatInfoMessage that = (ChatInfoMessage) o;

        return !(chatId != null ? !chatId.equals(that.chatId) : that.chatId != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChatInfoMessage{" +
                "chatId='" + chatId + '\'' +
                '}';
    }
}
