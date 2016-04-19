package arhangel.dim.core.commands;

import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.ChatHistoryResultMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

import java.util.List;

public class ChatHistoryCommand extends GenericCommand {
    private Type type = Type.MSG_CHAT_HIST;

    @Override
    public Message handleMessage(Session session, Message message) throws CommandException {
        ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;

        List<TextMessage> messages;
        Long chatId = chatHistoryMessage.getChatId();
        try {
            messages = session.getMessageStore().getMessagesFromChat(chatId);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }

        ChatHistoryResultMessage resultMessage = new ChatHistoryResultMessage();
        resultMessage.setHistory(messages);
        resultMessage.setChatId(chatId);
        return resultMessage;
    }
}
