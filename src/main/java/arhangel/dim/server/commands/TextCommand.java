package arhangel.dim.server.commands;

import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

public class TextCommand extends GenericCommand {
    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_TEXT;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        TextMessage textMessage = (TextMessage) message;
        Long chatId = textMessage.getChatId();
        try {
            Long messageId = session.getMessageStore().addTextMessage(chatId, textMessage);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        StatusMessage answer = new StatusMessage();
        answer.setText("Message sent");
        return answer;
    }
}
