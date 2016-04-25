package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComChatHist implements Command {
    private Integer chatId;

    public ComChatHist(Integer id){
        this.chatId = id;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {

    }
}
