package arhangel.dim.core.messages;

/**
 * Created by Арина on 19.04.2016.
 */
public class ChatListMessage extends Message {
    String chatList;
    public void setChatList(String list) { chatList = list; }
    public String getChatList() { return chatList; }
    @Override
    public String toString() {
        return "Your chat list: "+chatList;
    }
}
