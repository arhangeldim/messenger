package arhangel.dim.server.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.UserCreateMessage;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Session;

public class UserCreateCommand extends GenericCommand {
    @Override
    boolean checkMessage(Message message) {
        return message.getType() == Type.MSG_USER_CREATE;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException {
        UserCreateMessage userCreateMessage = (UserCreateMessage) message;
        String username = userCreateMessage.getUsername();
        String password = userCreateMessage.getPassword();

        User user;
        try {
            user = session.getUserStore().getUserByUsername(username);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        StatusMessage answer = new StatusMessage();
        if (user != null) {
            answer.setText(String.format("User \"%s\" already exists", username));
        } else {
            user = new User();
            user.setName(username);
            user.setPassword(password);
            Long userId;
            try {
                userId = session.getUserStore().addUser(user);
            } catch (StorageException e) {
                throw new CommandException("Database failed", e);
            }
            answer.setText(String.format("User \"%s\" created, id = %d", username, userId));
        }
        return answer;
    }
}
