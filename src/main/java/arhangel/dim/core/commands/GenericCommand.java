package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class GenericCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(GenericCommand.class);
    private Type type;

    abstract Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException;

    String getCommandName() {
        return this.getClass().toString();
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        log.info("Handling message {} with {}", message, getCommandName());
        Message answer = handleMessage(session, message);
        log.info("Sending answer: {}", answer);
        try {
            session.send(answer);
        } catch (ProtocolException | IOException e) {
            throw new CommandException("Could not send answer", e);
        }
    }

}
