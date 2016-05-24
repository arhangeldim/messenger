package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatListResultMessageCommand implements Command {

    @Override
    public void execute(Session session, Message msg) throws CommandException {
        try {
            ChatListResultMessage message = (ChatListResultMessage) msg;
            System.out.println("Chats: ");
            for (Long chatId: message.getChats()) {
                System.out.println(chatId);
            }
        } catch (ClassCastException e) {
            throw new CommandException(e);
        }
    }
}
