package arhangel.dim.core.messages;

import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatListCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(ChatListCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            MessageStore messageStore = session.getServer().getMessageStore();

            ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
            chatListResultMessage.setSenderId(null);
            chatListResultMessage.setType(Type.MSG_CHAT_LIST_RESULT);
            chatListResultMessage.setChatIds(messageStore.getChatsByUserId(session.getUser().getId()));

            log.info("Sending chats {} to {}", chatListResultMessage.getChatIds(), session.getUser().getLogin());
            try {
                session.send(chatListResultMessage);
            } catch (Exception e) {
                log.error("Couldn't reply to chat list command", e);
                throw new CommandException("Couldn't reply to chat list command");
            }
            return;
        }
        log.info("User requested chat list command without authenticating first");
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        response.setSenderId(null);
        response.setText("You have to log in first");
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to chat list command", e);
            throw new CommandException("Couldn't reply to chat list command");
        }
    }
}
