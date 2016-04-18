package arhangel.dim.server.commands;

import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.ChatHistoryResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

import java.util.List;

public class ChatHistoryCommand extends GenericCommand {


    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_CHAT_HIST;
    }

    @Override
    public Message handleMessage(Session session, Message message) throws CommandException {
        ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;

        List<TextMessage> messages;
        try {
            messages = session.getMessageStore().getMessagesByChatId(chatHistoryMessage.getChatId());
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }

        ChatHistoryResultMessage resultMessage = new ChatHistoryResultMessage();
        resultMessage.setHistory(messages);
        return resultMessage;
    }
}
