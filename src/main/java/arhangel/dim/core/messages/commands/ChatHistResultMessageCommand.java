package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatHistResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextClientMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class ChatHistResultMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        ChatHistResultMessage message = (ChatHistResultMessage) msg;
        List<TextClientMessage> msgs = message.getMessages();
        msgs.sort((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
        for (TextClientMessage current: msgs) {
            CommandByMessage.getCommand(Type.MSG_TEXT_CLIENT)
                    .execute(null, current);
        }
    }
}
