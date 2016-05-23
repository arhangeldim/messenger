package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

public class InfoCommand extends GenericCommand {

    private Type type = Type.MSG_INFO;

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        InfoMessage infoMessage = (InfoMessage) message;
        Long userId = infoMessage.getSenderId();
        User user = null;
        InfoResultMessage answer = new InfoResultMessage();
        try {
            user = session.getUserStore().getUserById(userId);
            answer.setInfo(" username: " + user.getName() + "; id: " + user.getId());
        } catch (StorageException e) {
            //throw new CommandException("Database failed", e);
            answer.setInfo(e.getMessage());
        }
        return answer;
    }
}
