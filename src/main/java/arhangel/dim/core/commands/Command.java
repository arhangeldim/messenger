package arhangel.dim.core.commands;


import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;

// TODO: на каждое сообщение завести обработчик-команду
public interface Command {

    public Message execute(Session session, Message message) throws CommandException;
}
