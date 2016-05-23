package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;

public class LoginCommand extends GenericCommand {
    private Type type = Type.MSG_LOGIN;

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        LoginMessage loginMessage = (LoginMessage) message;
        String username = loginMessage.getUsername();
        String password = loginMessage.getPassword();
        User user;
        try {
            user = session.getUserStore().getUser(username, password);
        } catch (StorageException e) {
            throw new CommandException("Storage failed", e);
        }

        StatusMessage answer = new StatusMessage();
        session.setUser(user);
        answer.setId(user.getId());
        answer.setUsername(user.getName());
        answer.setText(String.format("Logged in as %s, id#%d", username, user.getId()));
        return answer;
    }
}
