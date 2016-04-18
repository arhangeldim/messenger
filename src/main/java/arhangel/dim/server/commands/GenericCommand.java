package arhangel.dim.server.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.server.Session;

import java.io.IOException;


public abstract class GenericCommand implements Command {
    private void sendAnswer(Message answer, Session session) throws CommandException {
        try {
            session.send(answer);
        } catch (ProtocolException | IOException e) {
            throw new CommandException("Could not send answer", e);
        }
    }

    abstract boolean checkMessage(Message message);

    abstract Message handleMessage(Session session, Message message) throws CommandException;

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (!checkMessage(message)) {
            throw new CommandException("Message is wrong formatted");
        }
        Message answer = handleMessage(session, message);
        sendAnswer(answer, session);
    }
}
