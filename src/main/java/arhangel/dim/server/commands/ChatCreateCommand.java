package arhangel.dim.server.commands;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;


public class ChatCreateCommand extends GenericCommand {


    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_CHAT_CREATE;
    }

    @Override
    public Message handleMessage(Session session, Message message) throws CommandException {
        ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
        //TODO old chat for 2 users
        Long chatId = null;
        try {
            chatId = session.getMessageStore().addChat(chatCreateMessage.getParticipants());
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        StatusMessage answerMessage = new StatusMessage();
        answerMessage.setText(
                String.format("Chat #%d with participants %s created\n",
                        chatId,
                        chatCreateMessage.getParticipants().toString()
                ));

        return answerMessage;
    }


}
