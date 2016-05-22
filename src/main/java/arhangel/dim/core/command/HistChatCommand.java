package arhangel.dim.core.command;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.HistChatMessage;
import arhangel.dim.core.messages.HistChatResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by tatiana on 14.05.16.
 */
public class HistChatCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public HistChatCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            HistChatMessage chatHistoryMessage = (HistChatMessage) message;

            Long chatId = chatHistoryMessage.getChatId();

            StatusMessage errorMessage = new StatusMessage();

            User user = session.getUser();
            if (user == null) {
                log.info("HistChat: user not authorised ");
                errorMessage.setStatus("You are not authorised");
                session.send(errorMessage);
                return;
            }

            MessageStore messageStore = server.getMessageStore();

            if (!messageStore.getChatById(chatId).getParticipantIds().contains(user.getId())) {
                log.info(String.format("HistChat: user %d not invited to chat %d", user.getId(), chatId));
                errorMessage.setStatus("user with id=" + user.getId() + " wasn't invited to chat with id=" + chatId);
                session.send(errorMessage);
                return;
            }

            log.info(String.format("User %d: chat %d history", user.getId(), chatId));
            List<Long> messagesFromChat = messageStore.getMessagesFromChat(chatId);

            StringBuilder chatHistory = new StringBuilder();

            for (long i = 0; i < messagesFromChat.size(); ++i) {
                Long id = messagesFromChat.get((int) i);
                chatHistory.append(messageStore.getMessageById(messagesFromChat.get((int) i)).toString() + "\n");
            }

            HistChatResultMessage histChatResultMessage = new HistChatResultMessage();
            histChatResultMessage.setChatId(chatId);
            histChatResultMessage.setHistory(chatHistory.toString());
            session.send(histChatResultMessage);
            return;

        } catch (IOException | ProtocolException e) {
            throw new CommandException(e);
        }
    }



}
