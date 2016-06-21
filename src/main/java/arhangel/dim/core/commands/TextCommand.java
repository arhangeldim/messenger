package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.core.store.UserStore;

import java.util.List;


public class TextCommand implements Command {

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        TextMessage textMessage = (TextMessage) message;
        if (session.getUser() == null) {
            return new StatusMessage("Sign in please");
        }
        MessageStore messageStore = session.getMessageStore();
        UserStore userStore = session.getUserStore();
        try {
            List<Long> chats = userStore.getChatListByUser(session.getUser());
            if (!chats.contains(textMessage.getChatId())) {
                return new StatusMessage("You cant write in this chat");
            }
            messageStore.addMessage(session.getUser().getId(), textMessage.getChatId(), textMessage.getText());
        } catch (StorageException e) {
            throw new CommandException(e);
        }
        return  new StatusMessage("Success");
    }
}
