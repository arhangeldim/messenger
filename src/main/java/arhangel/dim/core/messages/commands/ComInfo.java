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
public class ComInfo implements Command {
    private int userId;

    public ComInfo(int id) {
        this.userId = id;
    }

    public ComInfo() { //info command without parameters
        this.userId = -1;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        //если пользователь аутентифицирован?
        TextMessage mes = (TextMessage) message;
        String[] tokens = mes.getText().split(" ");
        UserStoreImpl storage = (UserStoreImpl) session.getUserStorage();
        if (tokens.length > 0) {
            try {
                String id = tokens[0];
                Long identifier = Long.parseLong(id);
                User user = storage.getUserById(identifier);
                if (user != null) {
                    StatusMessage response = new StatusMessage();
                    response.setText(String.format("Info about user, %s", user.getName()));
                    session.send(response);
                }
            } catch (NumberFormatException exc) {
                throw new CommandException("Wrong id format");
            }
        } else {
            //session.currentUser;
        }
    }
}
