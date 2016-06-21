package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.core.store.UserStore;


public class ChatCreateCommand implements Command {

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        if (session.getUser() == null) {
            return new StatusMessage("Sign in please");
        }
        try {
            ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
            Long[] participants = chatCreateMessage.getParticipants();
            User buf;
            UserStore userStore = session.getUserStore();
            for (int i = 0; i < participants.length; i++) {
                buf = userStore.getUserById(participants[i]);
                if (buf == null) {
                    return new StatusMessage("User " + participants[i] + " does not exist");
                }
            }
            MessageStore messageStore = session.getMessageStore();
            Long chatId = messageStore.addChat(session.getUser().getId(), participants);
            return  new StatusMessage("Chat successfully created. Id: " + chatId);
        } catch (StorageException e) {
            throw new CommandException(e);
        }
    }
}
