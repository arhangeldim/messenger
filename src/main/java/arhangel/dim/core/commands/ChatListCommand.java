package arhangel.dim.core.commands;

import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

import java.util.List;


public class ChatListCommand implements Command {

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        ChatListMessage chatListMessage = (ChatListMessage) message;
        if (session.getUser() == null) {
            return new StatusMessage("Sign in please");
        }
        try {
            List<Long> chatList = session.getUserStore().getChatListByUser(session.getUser());
            return new ChatListResultMessage(chatList);
        } catch (StorageException e) {
            throw new CommandException(e);
        }
    }
}
