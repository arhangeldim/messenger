package arhangel.dim.core.messages;

import java.util.List;

public class ChatHistoryResultMessage extends Message {
    private List<Long>  messagesInChatId;

    public ChatHistoryResultMessage(List<Long> messagesInChatId) {
        this.messagesInChatId = messagesInChatId;
        type = Type.MSG_CHAT_HIST_RESULT;
    }

    public void setMessagesInChatId(List<Long> messagesInChatId) {
        this.messagesInChatId = messagesInChatId;
    }

    public List<Long> getMessagesInChatId() {
        return messagesInChatId;
    }
}
