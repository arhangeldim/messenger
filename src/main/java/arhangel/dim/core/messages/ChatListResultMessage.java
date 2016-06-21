package arhangel.dim.core.messages;

import java.util.List;

public class ChatListResultMessage extends Message {
    private List<Long> chatList;

    public ChatListResultMessage() {
        this.setType(Type.MSG_CHAT_LIST_RESULT);
    }

    public ChatListResultMessage(List<Long> chatList) {
        this.setType(Type.MSG_CHAT_LIST_RESULT);
        this.chatList = chatList;
    }

    public List<Long> getChatList() {
        return chatList;
    }

    public void setChatList(List<Long> chatList) {
        this.chatList = chatList;
    }

    @Override
    public String toString() {
        if (chatList.isEmpty()) {
            return "Your chat list is empty";
        }
        String result = "Your chat list:";
        for (Long chatId: chatList) {
            result += " " + chatId;
        }
        return result;
    }
}
