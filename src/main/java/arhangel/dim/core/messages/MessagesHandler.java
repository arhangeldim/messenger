package arhangel.dim.core.messages;

import arhangel.dim.commands.*;
import arhangel.dim.session.Session;
import arhangel.dim.server.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by olegchuikin on 18/04/16.
 */
public class MessagesHandler {

    private Map<Class<? extends Message>, Command> commands;

    public MessagesHandler(Server server) {
        commands = new HashMap<>();

        commands.put(TextMessage.class, new TextMessageCommand(server));
        commands.put(LoginMessage.class, new LoginMessageCommand(server));
        commands.put(ChatListMessage.class, new ChatListMessageCommand(server));
        commands.put(ChatCreateMessage.class, new ChatCreateMessageCommand(server));
        commands.put(InfoMessage.class, new InfoMessageCommand(server));
        commands.put(ChatHistMessage.class, new ChatHistMessageCommand(server));
    }

    public void execute(Session session, Message message) throws CommandException {
        if (!commands.containsKey(message.getClass())) {
            throw new CommandException("There isn't command for " + message.getClass());
        }
        commands.get(message.getClass()).execute(session, message);
    }

}
