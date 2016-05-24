package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.util.List;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatListMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        if (session.getUser() == null) {

            session.send(StatusMessage.logInFirstMessage());

        } else {
            msg.setSenderId(session.getUser().getId());

            List<Long> chatList = session.getServer().getMessageStore()
                    .getChatsByUserId(session.getUser().getId());
            ChatListResultMessage response = new ChatListResultMessage();
            response.setChats(chatList);

            session.send(response);

        }
    }
}
