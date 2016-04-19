package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.Message;

public class ChatHistoryCommand extends ClientCommandHandler {
    public ChatHistoryCommand(String name) {
        super(name);
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        this.checkUserLogin(user);

        if (input.length != 2) {
            throw new ClientInputException("Wrong parameters");
        }

        Long chatId;
        try {
            chatId = Long.parseUnsignedLong(input[1]);
        } catch (Exception e) {
            throw new ClientInputException("Chat id is not positive number");
        }

        return new ChatHistoryMessage(chatId);
    }

    @Override
    String getCommandHelpString() {
        return String.format("Usage: %s %s", getCommandName(), "<chat_id>");
    }
}
