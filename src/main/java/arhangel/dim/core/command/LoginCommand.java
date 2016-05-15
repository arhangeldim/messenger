package arhangel.dim.core.command;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStoreImpl;

import java.io.IOException;

/**
 * Created by tatiana on 19.04.16.
 */
public class LoginCommand implements Command {

    @Override
    public void execute(Session session, Message message) throws CommandException {
        LoginMessage loginMsg = (LoginMessage) message;

        String userName = loginMsg.getLogin();
        String password = loginMsg.getPassword();

        UserStoreImpl userStore = session.getUserStore();
        MessageStoreImpl messageStore = session.getMessageStore();

        TextMessage result = new TextMessage();
        if (!userStore.isUserExist(userName)) {
            result.setText("User not found");
            try {
                session.send(result);
            } catch (IOException | ProtocolException e) {
                e.printStackTrace();
            }
            return;
        }

        User user = userStore.getUser(userName, password);
        if (user == null) {
            result.setText("Login or password is incorrect");
        } else {
            result.setText(String.format("Success: userid: %d, name: %d", user.getId(), user.getName()));
        }

        try {
            session.send(result);
        } catch (IOException | ProtocolException e) {
            e.printStackTrace();
        }
    }
}
