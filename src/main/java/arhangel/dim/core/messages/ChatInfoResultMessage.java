package arhangel.dim.core.messages;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 23/05/16.
 */
public class ChatInfoResultMessage extends Message {

    private Long chatId;

    private List<Long> userIds;

    public ChatInfoResultMessage() {
        super(Type.MSG_CHAT_INFO_RESULT);
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

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

        ChatInfoResultMessage that = (ChatInfoResultMessage) o;

        if (chatId != null ? !chatId.equals(that.chatId) : that.chatId != null) return false;
        return !(userIds != null ? !userIds.equals(that.userIds) : that.userIds != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        result = 31 * result + (userIds != null ? userIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChatInfoResultMessage{" +
                "chat='" + chatId + '\'' +
                "users='" +
                String.join(",", userIds.stream().map(Object::toString).collect(Collectors.toList())) + '\'' +
                '}';
    }
}
