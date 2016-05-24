package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class InfoMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        InfoMessage message = (InfoMessage) msg;
        if (message.getUserId() == null) {
            if (session.getUser() != null) {
                message.setUserId(session.getUser().getId());
            } else {
                session.send(StatusMessage.logInFirstMessage());
                return;
            }
        }
        User user = session.getServer().getUserStore()
                .getUserById(message.getUserId());
        session.send(StatusMessage.userInfo(user, false));
    }
}
