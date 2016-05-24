package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;

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
