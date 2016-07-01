package arhangel.dim.core.command;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStoreImpl;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public TextCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {

            StatusMessage errorMessage = new StatusMessage();
            if (session.getUser() == null) {
                errorMessage.setStatus("Only authorised person can send messages");
                session.send(errorMessage);
                return;
            }

            MessageStoreImpl messageStore = (MessageStoreImpl)server.getMessageStore();

            TextMessage textMessage = (TextMessage)message;
            textMessage.setSenderId(session.getUser().getId());

            Chat chat = messageStore.getChatById(textMessage.getChatId());

            StatusMessage response = new StatusMessage();
            response.setStatus(String.format("User %s wrote to chat %d: %s",
                    session.getUser().getName(), chat.getId(), textMessage.getText()));
            response.setSenderId(textMessage.getSenderId());

            for (Long chatUsersId : chat.getParticipantIds()) {
                for (Session s : server.getSessions()) {
                    if (s.getUser() != null && chatUsersId.equals(s.getUser().getId())) {
                        s.send(response);
                    }
                }

            }

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

}
