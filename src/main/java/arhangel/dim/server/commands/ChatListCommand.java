package arhangel.dim.server.commands;

import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

import java.util.List;

public class ChatListCommand extends GenericCommand {


    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_CHAT_LIST;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        ChatListMessage chatListMessage = (ChatListMessage) message;
        Long userId = chatListMessage.getSenderId();
        List<Long> chats;
        try {
            chats = session.getMessageStore().getChatsByUserId(userId);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        ChatListResultMessage answer = new ChatListResultMessage();
        answer.setChats(chats);
        return answer;
    }
}
