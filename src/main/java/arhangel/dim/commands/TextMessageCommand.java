package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.UserDao;
import arhangel.dim.session.Session;
import arhangel.dim.server.Server;

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
                StatusMessage errorMessage = new StatusMessage();
                errorMessage.setText("You should login before you can create chat");
                session.send(errorMessage);
                return;
            }

            TextMessage msg = (TextMessage) message;
            msg.setSenderId(session.getUser().getId());

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            UserDao userDao = (UserDao) server.getDbFactory().getDao(User.class);
            Chat chat = chatDao.getByPK(msg.getChatId());

            StatusMessage response = new StatusMessage();
            response.setText(String.format("By user %s to chat %d: %s",
                    session.getUser().getName(), chat.getId(), msg.getText()));
            response.setSenderId(msg.getSenderId());
            response.setType(MSG_STATUS);

            if (!chat.getAdmin().getId().equals(session.getUser().getId())) {
                User chatAdmin = userDao.getByPK(chat.getAdmin().getId());
                for (Session adminSession : server.getSessionsManager().getSessionsByUserId(chatAdmin.getId())) {
                    adminSession.send(response);
                }
            }

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

}
