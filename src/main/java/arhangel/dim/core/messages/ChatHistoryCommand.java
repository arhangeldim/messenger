package arhangel.dim.core.messages;

import arhangel.dim.core.Chat;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(ChatListCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;
            MessageStore messageStore = session.getServer().getMessageStore();

            Chat chat = messageStore.getChatById(chatHistoryMessage.getChatId());
            if ((chat == null) || !chat.getUserIds().contains(session.getUser().getId())) {
                log.info("Chat {} not found for history {}, or user not in chat",
                        chatHistoryMessage.getChatId(),
                        chatHistoryMessage);
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText(String.format("You are not in chat %d", chatHistoryMessage.getChatId()));
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to chat history command", e);
                    throw new CommandException("Couldn't reply to chat history command");
                }
                return;
            }
            //Chat exists and includes user
            ChatHistoryResultMessage chatHistoryResultMessage = new ChatHistoryResultMessage();
            chatHistoryResultMessage.setType(Type.MSG_CHAT_HIST_RESULT);
            chatHistoryResultMessage.setSenderId(null);

            List<Long> messageIds = messageStore.getMessagesFromChat(chat.getId());
            List<TextMessage> messages = new ArrayList<>();
            for (Long messageId : messageIds) {
                TextMessage historyMessage = messageStore.getMessageById(messageId);
                messages.add(historyMessage);
            }
            chatHistoryResultMessage.setMessages(messages);
            log.info("Sending history {} to {}", chatHistoryResultMessage, session.getUser().getLogin());
            try {
                session.send(chatHistoryResultMessage);
            } catch (Exception e) {
                log.error("Couldn't reply to chat history command", e);
                throw new CommandException("Couldn't reply to chat history command");
            }
            return;
        }
        log.info("User requested chat history command without authenticating first");
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
