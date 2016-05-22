package arhangel.dim.core.command;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.CreateChatMessage;
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
 * Created by tatiana on 28.04.16.
 */
public class CreateChatCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public CreateChatCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                StatusMessage errorMessage = new StatusMessage();
                errorMessage.setStatus("Ony authorised person can create chat");
                session.send(errorMessage);
                return;
            }

            MessageStore messageStore = session.getMessageStore();

            List<Long> participants = ((CreateChatMessage) message).getUsersIds();
            if (participants.size() == 1) {
                List<Long> chatsByUser = messageStore.getChatsByUserId(session.getUser().getId());

                for (Long chatId : chatsByUser) {
                    Chat chat = messageStore.getChatById(chatId);
                    log.info("CHAT " + chat.toString());

                    if (chat.getParticipantIds().size() == 1 &&
                            chat.getParticipantIds().get(0).equals(participants.get(0))) {
                        StatusMessage response = new StatusMessage();
                        response.setStatus(String.format(
                                "Сhat with user %d already exists. Chat id: %d",
                                participants.get(0), chat.getId()));
                        session.send(response);
                        return;
                    }

                }

            }

            Chat chat = messageStore.createChat(session.getUser().getId(), participants);

            StatusMessage response = new StatusMessage();
            response.setStatus(String.format("Chat created. Chat id: %d", chat.getId()));
            session.send(response);
            return;


        } catch (ProtocolException | IOException e) {
            throw new CommandException(e);
        }
    }
}
