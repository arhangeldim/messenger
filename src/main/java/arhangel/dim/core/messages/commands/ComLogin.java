package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
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
    public static void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        TextMessage mes = (TextMessage) message;
        String[] tokens = mes.getText().split(" ");
        String name = tokens[0];
        String pass = tokens[1];
        UserStoreImpl storage = (UserStoreImpl) session.getUserStorage();
        User user = storage.getUser(name, pass);
        if (user != null) {
            StatusMessage response = new StatusMessage();
            response.setText(String.format("Hello, %s", name));
            session.authUser(user);
            session.send(response);
        }
    }
}
