package arhangel.dim.core.messages;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class TextCommand implements Command {
    public void execute(Session session, Message message)  throws CommandException {
        User user = session.getUser();
        if (user == null) {
            TextMessage ansMessage = new TextMessage();
            ansMessage.setType(Type.MSG_STATUS);
            ansMessage.setText("You have to log in");
            try {
                session.send(ansMessage);
            } catch (ProtocolException | IOException e) {
                e.printStackTrace();
            }
        } else {
            TextMessage redirectMessage = new TextMessage();
            redirectMessage.setSenderId(user.getId());
            redirectMessage.setText(((TextMessage) message).getText());
            Long chatId = ((TextMessage) message).getChatId();
            Chat chat = session.getServer().getMessageStore()
                    .getChatById(chatId);
            for (Long userId : chat.getUsers()) {
                if (session.getServer().getActiveUsers().containsValue(userId)) {
                    try {
                        session.getServer().getActiveUsers().get(userId).send(message);
                        //NPE
                    } catch (ProtocolException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
