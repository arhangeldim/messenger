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
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public LoginCommand(Server server) {
        this.server = server;
    }

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
            session.setUser(user);
            result.setText(String.format("Success: userid: %d, name: %s", user.getId(), user.getName()));
        }

        try {
            session.send(result);
        } catch (IOException | ProtocolException e) {
            e.printStackTrace();
        }
    }
}
