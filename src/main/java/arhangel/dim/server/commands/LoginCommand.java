package arhangel.dim.server.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

public class LoginCommand extends GenericCommand {

    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_LOGIN;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        LoginMessage loginMessage = (LoginMessage) message;
        String username = loginMessage.getUsername();
        String password = loginMessage.getPassword();
        User user;
        try {
            user = session.getUserStore().getUserByUsername(username);
        } catch (StorageException e) {
            throw new CommandException("Storage failed", e);
        }

        StatusMessage answer = new StatusMessage();
        if (user == null || !user.passwordMatch(password)) {
            answer.setText("Login or password is wrong");
        } else {
            session.setUser(user);
            answer.setId(user.getId());
            answer.setUsername(user.getName());
            answer.setText(String.format("Logged in as %s, id#%d", username, user.getId()));
        }
        return answer;
    }
}
