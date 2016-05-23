package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.ChatInfoMessage;
import arhangel.dim.core.messages.ChatInfoResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.server.Server;
import arhangel.dim.session.Session;

import java.io.IOException;

/**
 * Created by olegchuikin on 23/05/16.
 */
public class ChatInfoMessageCommand implements Command {

    private Server server;

    public ChatInfoMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                sendError(session, "You should login before you can see chats info");
                return;
            }

            ChatInfoMessage msg = (ChatInfoMessage) message;

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            Chat chat = chatDao.getByPk(msg.getChatId());

            ChatInfoResultMessage result = new ChatInfoResultMessage();
            result.setUserIds(chat.getParticipants());
            result.setChatId(chat.getId());

            session.send(result);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    private void sendError(Session session, String message) throws IOException, ProtocolException {
        StatusMessage errorMessage = new StatusMessage();
        errorMessage.setText(message);
        session.send(errorMessage);
    }
}
