package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.MessageDao;
import arhangel.dim.core.store.dao.UserDao;
import arhangel.dim.session.Session;
import arhangel.dim.server.Server;

import java.io.IOException;

import static arhangel.dim.core.messages.Type.MSG_STATUS;

/**
 * Created by olegchuikin on 18/04/16.
 */
public class TextMessageCommand implements Command {

    private Server server;

    public TextMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                sendError(session, "You should login before you can send message");
                return;
            }

            TextMessage msg = (TextMessage) message;
            msg.setSenderId(session.getUser().getId());

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            Chat chat = chatDao.getByPK(msg.getChatId());
            if (chat == null){
                sendError(session, "There isn't chat with id " + msg.getChatId());
                return;
            }

            MessageDao messageDao = (MessageDao) server.getDbFactory().getDao(TextMessage.class);
            msg.setTimestamp(System.currentTimeMillis());
            msg = messageDao.persist(msg);

            StatusMessage response = new StatusMessage();
            response.setText(String.format("By user %s to chat %d: %s",
                    session.getUser().getName(), chat.getId(), msg.getText()));
            response.setSenderId(msg.getSenderId());
            response.setType(MSG_STATUS);

            for (Long destId : chat.getParticipants()) {
                for (Session s : server.getSessionsManager().getSessionsByUserId(destId)) {
                    if (s.getUser() != null && !s.getUser().getId().equals(msg.getSenderId())) {
                        s.send(response);
                    }
                }

            }

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
