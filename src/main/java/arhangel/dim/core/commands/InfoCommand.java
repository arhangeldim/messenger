package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.core.store.UserStore;

import java.util.List;


public class InfoCommand implements Command {

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        InfoMessage infoMessage = (InfoMessage) message;
        if (session.getUser() == null) {
            return new StatusMessage("Sign in please");
        }
        if (infoMessage.getInfoUserId() == null) {
            return new InfoResultMessage(session.getUser().toString());
        }
        User infoUser;
        try {
            UserStore userStore = session.getUserStore();
            infoUser = userStore.getUserById(infoMessage.getInfoUserId());
        } catch (StorageException e) {
            throw new CommandException(e);
        }
        if (infoUser == null) {
            return new StatusMessage("User does not exist");
        }
        return new InfoResultMessage(infoUser.toString());
    }
}