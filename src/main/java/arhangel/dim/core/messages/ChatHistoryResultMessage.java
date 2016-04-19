package arhangel.dim.core.messages;

import java.util.List;

public class ChatHistoryResultMessage extends Message {
    private List<TextMessage> history;
    private Long chatId;

    public ChatHistoryResultMessage() {
        super();
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Chat history for (");
        builder.append(chatId);
        builder.append(")\n[\n");
        for (TextMessage text : history) {
            builder.append(text.getDate().toString());
            builder.append(": \"");
            builder.append(text.getText());
            builder.append("\"\n");
        }
        builder.append("]\n");
        return builder.toString();
    }

    public List<TextMessage> getHistory() {
        return history;
    }

    public void setHistory(List<TextMessage> history) {
        this.history = history;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
