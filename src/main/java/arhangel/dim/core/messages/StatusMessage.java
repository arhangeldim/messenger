package arhangel.dim.core.messages;

import arhangel.dim.core.User;

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

    public static StatusMessage wrongChatMessage() {
        StatusMessage sendMessage = new StatusMessage();
        sendMessage.setText("Wrong chat");
        return sendMessage;
    }

    public static StatusMessage userInfo(User user, boolean full) {
        StatusMessage sendMessage = new StatusMessage();
        String response;
        if (user != null) {
            response = "Id: " + user.getId().toString() + "\n" +
                    "Login: " + user.getName().toString() + "\n";
            if (full) {
                response += "Password: " + user.getPassword() + "\n";
            }
        } else {
            response = "No such user";
        }
        sendMessage.setText(response);
        return sendMessage;
    }

}
