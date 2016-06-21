package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;
import arhangel.dim.server.Server;
import org.slf4j.Logger;



public class CommandExecutor {
    Logger log = Server.log;

    public Message processCommand(Message msg, Session session) throws CommandException {
        switch (msg.getType()) {
            case MSG_LOGIN:
                return new LoginCommand().execute(session, msg);
            case MSG_TEXT:
                return new TextCommand().execute(session, msg);
            case MSG_INFO:
                return new InfoCommand().execute(session, msg);
            case MSG_CHAT_LIST:
                return new ChatListCommand().execute(session, msg);
            case MSG_CHAT_CREATE:
                return new ChatCreateCommand().execute(session, msg);
            case MSG_CHAT_HIST:
                return new ChatHistCommand().execute(session, msg);
            default:
                throw new CommandException("Unknown command type");
        }
    }
}
