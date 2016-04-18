package arhangel.dim.core.messages;

import java.util.List;

public class ChatHistoryResultMessage extends Message {
    private List<TextMessage> history;

    ChatHistoryResultMessage() {
        super();
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    public List<TextMessage> getHistory() {
        return history;
    }

    public void setHistory(List<TextMessage> history) {
        this.history = history;
    }
}
