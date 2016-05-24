package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Type;

import java.util.HashMap;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class CommandByMessage {
    private static HashMap<Type, Command> messageToCommand;

    static {
        messageToCommand = new HashMap<>();
        messageToCommand.put(Type.MSG_LOGIN, new LoginMessageCommand());
        messageToCommand.put(Type.MSG_TEXT, new TextMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_LIST, new ChatListMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_LIST_RESULT,
                             new ChatListResultMessageCommand());
        messageToCommand.put(Type.MSG_STATUS, new StatusMessageCommand());
        messageToCommand.put(Type.MSG_TEXT_CLIENT,
                new TextClientMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_HIST, new ChatHistMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_HIST_RESULT,
                new ChatHistResultMessageCommand());
        messageToCommand.put(Type.MSG_CHAT_CREATE,
                new ChatCreateMessageCommand());
        messageToCommand.put(Type.MSG_INFO, new InfoMessageCommand());
    }

    public static Command getCommand(Type type) {
        return messageToCommand.get(type);
    }
}
