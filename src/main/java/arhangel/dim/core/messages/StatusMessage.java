package arhangel.dim.core.messages;

import static arhangel.dim.core.messages.Type.MSG_STATUS;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class StatusMessage extends Message {
    private String text;

    public StatusMessage() {
        super(MSG_STATUS);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "id='" + getId() + '\'' +
                "text='" + text + '\'' +
                '}';
    }
}
