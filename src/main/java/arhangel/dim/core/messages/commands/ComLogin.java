package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserStoreImpl;

import java.io.IOException;


/**
 * Created by dmitriy on 25.04.16.
 */
public class ComLogin implements Command {

    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        LoginMessage mes = (LoginMessage) message;
        UserStoreImpl storage = (UserStoreImpl) session.getUserStore();
        User user = storage.getUser(mes.getLogin(), mes.getPassword());
        StatusMessage response = new StatusMessage();
        if (user != null) {
            response.setStatus(StatusCode.LoggingInSucceed);
            session.authUser(user);
            session.send(response);
        } else {
            response.setStatus(StatusCode.LoggingInFailed);
            session.send(response);
        }
    }
}
