package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.UserDao;
import arhangel.dim.server.Server;
import arhangel.dim.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static arhangel.dim.core.messages.Type.MSG_STATUS;

/**
 * Created by olegchuikin on 02/05/16.
 */
public class InfoMessageCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(InfoMessageCommand.class);

    public InfoMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                StatusMessage errorMessage = new StatusMessage();
                errorMessage.setText("You should login before you can get info");
                session.send(errorMessage);
                return;
            }

            InfoMessage msg = (InfoMessage) message;
            if (msg.getTarget().equals(-1L)) {
                msg.setTarget(session.getUser().getId());
            }

            UserDao userDao = (UserDao) server.getDbFactory().getDao(User.class);

            InfoResultMessage response = new InfoResultMessage();
            response.setType(Type.MSG_INFO_RESULT);
            User target = userDao.getByPk(msg.getTarget());
            if (target == null) {
                StatusMessage errMsg = new StatusMessage();
                errMsg.setType(MSG_STATUS);
                errMsg.setText("There is no user with such id!");
                session.send(errMsg);
                return;
            }
            response.setName(target.getName());

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            List<Chat> chatsByAdmin = chatDao.getChatsByAdmin(target);

            if (chatsByAdmin != null && chatsByAdmin.size() > 0) {
                response.setChats(chatsByAdmin.stream().map(Chat::getId).collect(Collectors.toList()));
            }
            response.setUserId(target.getId());
            session.send(response);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
