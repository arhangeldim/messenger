package arhangel.dim.core.messages;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ChatCreateMessage extends Message {

    private List<Long> userIds;

    public ChatCreateMessage() {
        super(Type.MSG_CHAT_CREATE);
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        ChatCreateMessage that = (ChatCreateMessage) object;

        return !(userIds != null ? !userIds.equals(that.userIds) : that.userIds != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userIds != null ? userIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "chats='" +
                String.join(",", userIds.stream().map(Object::toString).collect(Collectors.toList())) + '\'' +
                '}';
    }
}
