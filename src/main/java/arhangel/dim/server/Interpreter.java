package arhangel.dim.server;

import arhangel.dim.core.commands.GenericCommand;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private final Map<Type, GenericCommand> commands;

    public Interpreter(Map<Type, GenericCommand> commands) {
        this.commands = commands;
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
