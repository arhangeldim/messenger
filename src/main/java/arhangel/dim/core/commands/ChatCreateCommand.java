package arhangel.dim.core.commands;

import arhangel.dim.core.commands.GenericCommand;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

public class ChatCreateCommand extends GenericCommand {
    private Type type= Type.MSG_CHAT_CREATE;

    @Override
    public Message handleMessage(Session session, Message message) throws CommandException {
        ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
        Long chatId;
        try {
            chatId = session.getMessageStore().addChat(chatCreateMessage.getParticipants());
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        StatusMessage answerMessage = new StatusMessage();
        answerMessage.setId(message.getId());
        answerMessage.setText(
                String.format("Chat #%d with participants %s created\n",
                        chatId,
                        chatCreateMessage.getParticipants().toString()
                ));

        return answerMessage;
    }


}
