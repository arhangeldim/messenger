package arhangel.dim.core.messages;

/**
 * Created by Арина on 19.04.2016.
 */
public class ChatHistResultMessage extends Message {
    private String result = "";
    private String chatId;

    public void setChatId(String id) {
        this.chatId = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void addMsg(String msg) {
        result = result + "\n" + msg;
    }

    @Override
    public String toString() {
        return "History of chat " +
                "id=" + chatId + result;
    }
}
