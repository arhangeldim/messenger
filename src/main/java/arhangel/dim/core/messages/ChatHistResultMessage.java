package arhangel.dim.core.messages;

import java.util.List;

public class ChatHistResultMessage extends Message {
    private List<String> text;
    private Long chatId;

    public ChatHistResultMessage(){
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    public ChatHistResultMessage(List<String> text, Long chatId) {
        this.setType(Type.MSG_CHAT_HIST_RESULT);
        this.text = text;
        this.chatId = chatId;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        if (text.isEmpty()) {
            return "No messages in this chat. ID: " + chatId;
        }
        String result = "Chat ID = " + chatId + " history:";
        for (String msgText: text) {
            result += "\n" + msgText;
        }
        return result;
    }
}
