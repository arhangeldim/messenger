package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.Message;

public class InfoCommand extends ClientCommandHandler {

    public InfoCommand(String name) {
        super(name);
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        this.checkUserLogin(user);

        Long userId;
        if (input.length == 1) {
            userId = user.getId();
        } else if (input.length == 2) {
            try {
                userId = Long.parseUnsignedLong(input[1]);
            } catch (Exception e) {
                throw new ClientInputException("Error: Could not parse user id");
            }

        } else {
            throw new ClientInputException("Too many arguments");
        }

        InfoMessage result = new InfoMessage();
        result.setId(user.getId());
        result.setAboutId(userId);

        return result;
    }

    @Override
    String getCommandHelpString() {
        return String.format("Usage: %s %s", getCommandName(), "<user_id>");
    }
}
