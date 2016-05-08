package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.Session;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComText implements Command {
    private int chatId;
    private TextMessage mes; //current message

    public ComText(int id, TextMessage text){
        this.chatId = id;
        this.mes = text;
    }

    @Override
    public static void execute(Session session, Message message) throws CommandException {

    }
}
