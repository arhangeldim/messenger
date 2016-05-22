package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.session.Session;
import arhangel.dim.server.Server;
import arhangel.dim.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ChatCreateMessageCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(ChatCreateMessageCommand.class);

    public ChatCreateMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                StatusMessage errorMessage = new StatusMessage();
                errorMessage.setText("You should login before you can create chat");
                session.send(errorMessage);
                return;
            }

            // TODO
//            MessageStore messageStore = null;//server.getDbFactory().getMessageStore();

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);

            List<Long> participants = ((ChatCreateMessage) message).getUserIds();
            if (participants.size() == 1) {
                log.info("1 partitioner");
//                List<Long> chatsByUser = messageStore.getChatsByUserId(session.getUser().getId());
                List<Chat> chatsByUser = chatDao.getChatsByAdmin(session.getUser());
                if (chatsByUser != null) {
                    for (Chat chat : chatsByUser) {
                        log.info("CHAT " + chat.toString());
                        if (chat.getParticipants().size() == 2 &&
                                chat.getParticipants().contains(session.getUser().getId())
                                && chat.getParticipants().contains(participants.get(0))) {
                            StatusMessage response = new StatusMessage();
                            response.setText(String.format(
                                    "You already have chat with user %d. Chat id: %d",
                                    participants.get(0), chat.getId()));
                            session.send(response);
                            return;
                        }
                    }
                }

            }

            Chat chat = new Chat();
            chat.setAdmin(session.getUser());
            chat.setParticipants(participants);
            chat.getParticipants().add(session.getUser().getId());
            chat = chatDao.persist(chat);

            StatusMessage response = new StatusMessage();
            response.setText(String.format("New chat with %s was created. Chat id: %d",
                    String.join(",", ParseUtils.longListToStringArr(participants)), chat.getId()));
            session.send(response);

        } catch (PersistException | ProtocolException | IOException e) {
            throw new CommandException(e);
        }
    }
}
