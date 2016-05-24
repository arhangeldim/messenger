package arhangel.dim.core.messages;

import arhangel.dim.core.Chat;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(TextCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            TextMessage textMessage = (TextMessage) message;
            MessageStore messageStore = session.getServer().getMessageStore();
            Chat chat = messageStore.getChatById(textMessage.getChatId());
            if (chat == null || !chat.getUserIds().contains(session.getUser().getId())) {
                log.info("Chat {} not found for message {}, or user not in chat", textMessage.getChatId(), textMessage);
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText(String.format("You are not in chat %d", textMessage.getChatId()));
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to text command", e);
                    throw new CommandException("Couldn't reply to text command");
                }
                return;
            }
            //Chat exists with requester in it
            TextMessage multicastTextMessage = messageStore.addMessage(textMessage);
            log.info("Message saved {}", multicastTextMessage);

            multicastTextMessage.setSenderLogin(session.getUser().getLogin());

            synchronized (session.getServer().getSessions()) {
                session.getServer().getSessions().stream()
                        .filter(serverSession
                                -> (serverSession.getUser() != null) &&
                                (chat.getUserIds().contains(serverSession.getUser().getId())))
                        .forEach(serverSession -> {
                            try {
                                serverSession.send(multicastTextMessage);
                            } catch (Exception e) {
                                log.error("Couldn't reply to text command", e);
                                //throw new CommandException("Couldn't reply to chat create command");
                            }
                        });
            }
            log.info("Message multicast {}", multicastTextMessage);
            return;
        }
        log.info("User requested text command without authenticating first");
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        response.setSenderId(null);
        response.setText("You have to log in first");
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to text command", e);
            throw new CommandException("Couldn't reply to text command");
        }
    }
}
