package arhangel.dim.server.commands;

import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

/**
 * Created by kontr on 16.04.16.
 */
public class InfoCommand extends GenericCommand {

    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_INFO;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        InfoMessage infoMessage = (InfoMessage) message;
        Long userId = infoMessage.getSenderId();
        String info = null;
        try {
            info = session.getUserStore().getUserInformation(userId);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        InfoResultMessage answer = new InfoResultMessage();
        answer.setInfo(info);
        return answer;
    }
}
