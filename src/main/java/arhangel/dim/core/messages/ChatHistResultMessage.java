package arhangel.dim.core.messages;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 22/05/16.
 */
public class ChatHistResultMessage extends Message{

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatHistResultMessage that = (ChatHistResultMessage) o;

        return !(messages != null ? !messages.equals(that.messages) : that.messages != null);

    }

    @Override
    public int hashCode() {
        return messages != null ? messages.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "chats='" +
                String.join(",", messages.stream().map(Object::toString).collect(Collectors.toList())) + '\'' +
                '}';
    }
}
