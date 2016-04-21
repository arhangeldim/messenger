package arhangel.dim.core.messages;

import arhangel.dim.commands.ChatCreateMessageCommand;
import arhangel.dim.commands.ChatListMessageCommand;
import arhangel.dim.commands.LoginMessageCommand;
import arhangel.dim.commands.TextMessageCommand;
import arhangel.dim.core.net.Session;
import arhangel.dim.server.Server;

/**
 * Created by olegchuikin on 18/04/16.
 */
public class MessagesHandler {

    private TextMessageCommand textMessageCommand;
    private LoginMessageCommand loginMessageCommand;
    private ChatListMessageCommand chatListMessageCommand;
    private ChatCreateMessageCommand chatCreateMessageCommand;

    public MessagesHandler(Server server) {
        textMessageCommand = new TextMessageCommand(server);
        loginMessageCommand = new LoginMessageCommand(server);
        chatListMessageCommand = new ChatListMessageCommand(server);
        chatCreateMessageCommand = new ChatCreateMessageCommand(server);
    }

    public void executeTextMessage(Session session, TextMessage message) throws CommandException {
        textMessageCommand.execute(session, message);
    }

    public void executeLoginMessage(Session session, LoginMessage message) throws CommandException {
        loginMessageCommand.execute(session, message);
    }

    public void executeChatListMessage(Session session, ChatListMessage message) throws CommandException {
        chatListMessageCommand.execute(session, message);
    }

    public void executeChatCreateMessage(Session session, ChatCreateMessage message) throws CommandException {
        chatCreateMessageCommand.execute(session, message);
    }

}
