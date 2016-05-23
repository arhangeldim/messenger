package arhangel.dim.core.messages;

import java.util.List;

public class ChatHistoryResultMessage extends Message {
    private List<TextMessage> messages;

    public List<TextMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatHistoryResultMessage{" +
                "messages=" + messages +
                "} " + super.toString();
    }
}
