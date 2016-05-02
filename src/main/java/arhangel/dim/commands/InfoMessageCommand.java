package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.PersistException;
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
            InfoMessage msg = (InfoMessage) message;
            if (msg.getTarget().equals(-1L)) {
                msg.setTarget(session.getUser().getId());
            }

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            UserDao userDao = (UserDao) server.getDbFactory().getDao(User.class);

            InfoResultMessage response = new InfoResultMessage();
            response.setType(Type.MSG_INFO_RESULT);
            User target = userDao.getByPK(msg.getTarget());
            if (target == null){
                StatusMessage errMsg = new StatusMessage();
                errMsg.setType(MSG_STATUS);
                errMsg.setText("There is no user with such id!");
                session.send(errMsg);
                return;
            }
            response.setName(target.getName());
            List<Chat> chatsByAdmin = chatDao.getChatsByAdmin(target);

            if (chatsByAdmin != null && chatsByAdmin.size() > 0) {
                response.setChats(chatsByAdmin.stream().map(Chat::getId).collect(Collectors.toList()));
            }
            session.send(response);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
