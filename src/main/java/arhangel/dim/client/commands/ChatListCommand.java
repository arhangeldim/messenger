package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.Message;

public class ChatListCommand extends ClientCommandHandler {

    public ChatListCommand(String name) {
        super(name);
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        this.checkUserLogin(user);
        ChatListMessage message = new ChatListMessage();
        message.setSenderId(user.getId());
        return message;
    }
}
