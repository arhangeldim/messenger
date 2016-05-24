package arhangel.dim.core.messages;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 22/05/16.
 */
public class ChatHistResultMessage extends Message {

    private List<TextMessage> messages;

    public ChatHistResultMessage() {
        super(Type.MSG_CHAT_HIST_RESULT);
    }

    public List<TextMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ChatHistResultMessage that = (ChatHistResultMessage) object;

        return !(messages != null ? !messages.equals(that.messages) : that.messages != null);

    }

    @Override
    public int hashCode() {
        return messages != null ? messages.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChatHistResultMessage{" +
                "chats='" +
                String.join(",", messages.stream().map(Object::toString).collect(Collectors.toList())) + '\'' +
                '}';
    }
}
