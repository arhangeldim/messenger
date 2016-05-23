package arhangel.dim.core.messages;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class TextClientMessage extends TextMessage {
    private String senderLogin;
    public TextClientMessage(TextMessage msg, String login) {
        setChatId(msg.getChatId());
        setType(Type.MSG_TEXT_CLIENT);
        setText(msg.getText());
        setId(msg.getId());
        setSenderId(msg.getSenderId());
        senderLogin = login;
        setTimestamp(getTimestamp());
    }

    @Override
    public String toString() {
        return "TextClientMessage{" +
                "textMessage=" + super.toString() +
                "senderLogin='" + senderLogin + '\'' +
                '}';
    }
}
