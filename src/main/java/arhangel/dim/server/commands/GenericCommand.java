package arhangel.dim.server.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public abstract class GenericCommand implements Command {

    private static Logger log = LoggerFactory.getLogger(GenericCommand.class);


    private void sendAnswer(Message answer, Session session) throws CommandException {
        log.info("Sending answer: {}", answer);
        try {
            session.send(answer);
        } catch (ProtocolException | IOException e) {
            throw new CommandException("Could not send answer", e);
        }
    }

    abstract boolean checkMessage(Message message);

    abstract Message handleMessage(Session session, Message message) throws CommandException;

    String getCommandName() {
        return this.getClass().toString();
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        log.info("Handling message {} with {}", message, getCommandName());
        if (!checkMessage(message)) {
            throw new CommandException("Message is wrong formatted");
        }
        Message answer = handleMessage(session, message);
        sendAnswer(answer, session);
    }
}
