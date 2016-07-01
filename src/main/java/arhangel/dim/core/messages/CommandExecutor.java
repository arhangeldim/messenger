package arhangel.dim.core.messages;

import arhangel.dim.core.net.Session;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private Map<Type, Command> commands;

    public CommandExecutor() {
        commands = new HashMap<>();
    }

    public CommandExecutor addCommand(Type type, Command command) {
        commands.put(type, command);
        return this;
    }

    public void handleMessage(Message message, Session session) throws CommandException {
        Type messageType = message.getType();
        if (messageType == null) {
            throw new CommandException("Message type is null");
        } else if (commands.containsKey(messageType)) {
            commands.get(messageType).execute(session, message);
        } else {
            throw new CommandException("Undefined message type");
        }
    }
}