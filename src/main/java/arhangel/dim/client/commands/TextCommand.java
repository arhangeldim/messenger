package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

public class TextCommand extends ClientCommandHandler {

    public TextCommand(String name) {
        super(name);
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        this.checkUserLogin(user);

        if (input.length < 3) {
            throw new ClientInputException("Not enough arguments");
        }

        Long chatId;

        try {
            chatId = Long.parseUnsignedLong(input[1]);
        } catch (Exception e) {
            throw new ClientInputException("Chat id specified wrong");
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < input.length; ++i) {
            stringBuilder.append(input[i]);
        }

        String text = stringBuilder.toString();

        return new TextMessage(chatId, text);
    }

    @Override
    String getCommandHelpString() {
        return String.format("Usage: %s %s", getCommandName(), "<text>");
    }
}
