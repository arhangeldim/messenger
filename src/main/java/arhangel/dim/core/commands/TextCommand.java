package arhangel.dim.core.commands;

import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

import java.util.Date;

public class TextCommand extends GenericCommand {
    private Type type = Type.MSG_TEXT;

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        TextMessage textMessage = (TextMessage) message;
        Long chatId = textMessage.getChatId();
        Long messageId;
        try {
            textMessage.setDate(new Date(System.currentTimeMillis()));
            messageId = session.getMessageStore().addMessage(chatId, textMessage);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        StatusMessage answer = new StatusMessage();
        answer.setId(messageId);
        answer.setText("Message sent");
        return answer;
    }
}
