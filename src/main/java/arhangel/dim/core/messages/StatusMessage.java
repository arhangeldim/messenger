package arhangel.dim.core.messages;

import java.time.LocalDateTime;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class StatusMessage extends TextMessage {
    public StatusMessage() {
        setType(Type.MSG_STATUS);
    }

    public static StatusMessage logInFirstMessage() {
        StatusMessage sendMessage = new StatusMessage();
        sendMessage.setText("Log in first");
        return sendMessage;
    }

}
