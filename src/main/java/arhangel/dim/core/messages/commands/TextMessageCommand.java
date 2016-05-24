package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextClientMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class TextMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg)
            throws CommandException {
        if (session.getUser() == null) {

            session.send(StatusMessage.logInFirstMessage());

        } else {
            msg.setSenderId(session.getUser().getId());

            TextMessage textMessage = (TextMessage) msg;
            textMessage.setTimestamp(LocalDateTime.now());
            Chat chat = session.getServer().getMessageStore()
                    .getChatById(textMessage.getChatId());
            if (!chat.getUsers().contains(session.getUser().getId())) {

                session.send(StatusMessage.wrongChatMessage());

            }

            session.getServer().getMessageStore()
                    .addMessage(textMessage.getChatId(), msg);

            for (Long userId: chat.getUsers()) {
                TextClientMessage outMessage
                        = new TextClientMessage(textMessage,
                        session.getUser().getName());
                Session cur = session.getServer().getActiveUsers().get(userId);
                if (cur != null) {
                    cur.send(outMessage);
                }
            }
        }
    }
}
