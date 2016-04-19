package arhangel.dim.commandHandler;

import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

/**
 * Created by Арина on 19.04.2016.
 */
public class CommandHandler {
    public Boolean ifErrorSend(Boolean result, Session session, String text) throws CommandException {
        try {
            if (!result) {
                StatusMessage errorMsg = new StatusMessage();
                errorMsg.setText(text);
                errorMsg.setType(Type.MSG_STATUS);
                session.send(errorMsg);
                return true;
            }
        } catch (ProtocolException e) {
            CommandException ex = new CommandException("ProtocolException");
            throw ex;
        } catch (IOException e) {
            CommandException ex = new CommandException("IOException");
            throw ex;
        }
        return false;
    }
}
