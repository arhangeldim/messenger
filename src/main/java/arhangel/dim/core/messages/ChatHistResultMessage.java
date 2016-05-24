package arhangel.dim.core.messages;

import javax.xml.soap.Text;
import java.util.List;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatHistResultMessage extends Message {
    private List<TextClientMessage> messages;

    public List<TextClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TextClientMessage> messages) {
        this.messages = messages;
    }

    public ChatHistResultMessage(List<TextClientMessage> msgs) {
        setType(Type.MSG_CHAT_HIST_RESULT);
        messages = msgs;
    }
}
