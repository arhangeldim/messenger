package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextClientMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class TextMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg)
            throws CommandException {
        if (session.getUser() == null) {
            try {
                session.send(StatusMessage.logInFirstMessage());
            } catch (ProtocolException | IOException e) {
                e.printStackTrace();
            }
        } else {
            msg.setSenderId(session.getUser().getId());

            TextMessage textMessage = (TextMessage) msg;

            Chat chat = session.getServer().getMessageStore()
                    .getChatById(textMessage.getChatId());

            msg = session.getServer().getMessageStore()
                    .addMessage(textMessage.getChatId(), msg);

            for (Long userId: chat.getUsers()) {
                TextClientMessage outMessage
                        = new TextClientMessage(textMessage,
                        session.getUser().getName());
                if (session.getServer().getActiveUsers().containsKey(userId)) {
                    try {
                        session.getServer().getActiveUsers().get(userId)
                                .send(outMessage);
                        //NPE
                    } catch (ProtocolException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
