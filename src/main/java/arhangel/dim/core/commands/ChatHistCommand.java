package arhangel.dim.core.commands;

import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatHistResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

import java.util.List;


public class ChatHistCommand implements Command {

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        ChatHistMessage chatHistMessage = (ChatHistMessage) message;
        if (session.getUser() == null) {
            return new StatusMessage("Sign in please");
        }
        try {
            List<String> textList = session.getMessageStore().getMessagesFromChat(chatHistMessage.getChatId());
            return new ChatHistResultMessage(textList, chatHistMessage.getChatId());
        } catch (StorageException e) {
            throw new CommandException(e);
        }
    }
}
