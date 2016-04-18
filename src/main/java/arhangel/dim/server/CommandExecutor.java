package arhangel.dim.server;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private Map<Type, Command> commands;

    CommandExecutor() {
        commands = new HashMap<>();
    }

    CommandExecutor addCommand(Type type, Command command) {
        commands.put(type, command);
        return this;
    }

    void handleMessage(Message message, Session session) throws CommandException {
        Type messageType = message.getType();
        if (messageType == null) {
            // TODO: 16.04.16 not expected
        } else if (commands.containsKey(messageType)) {
            commands.get(messageType).execute(session, message);
        } else {
            // TODO: 16.04.16 not found
        }
    }
}
