package arhangel.dim.client;

import arhangel.dim.client.commands.ClientCommandHandler;
import arhangel.dim.core.messages.Message;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

class ClientMessageCreator {

    private Map<String, ClientCommandHandler> shellCommands;
    private PrintStream userOutput;

    ClientMessageCreator() {
        shellCommands = new HashMap<>();
        userOutput = System.out;
    }

    ClientMessageCreator(PrintStream userOutput) {
        super();
        this.userOutput = userOutput;
    }

    /**
     * Add new handler or overwrite old one.
     *
     * @param handler handler of the command
     */
    ClientMessageCreator addHandler(ClientCommandHandler handler) {
        shellCommands.put(handler.getCommandName(), handler);
        return this;
    }

    private String getHelp() {
        StringBuilder builder = new StringBuilder();
        builder.append("/help           : Write all available commands\n");
        for (ClientCommandHandler handler : shellCommands.values()) {
            builder.append(handler.getCommandHelp());
            builder.append('\n');
        }
        return builder.toString();
    }

    Message handleCommandline(String input, ClientUser currentClient) {
        String[] tokens = input.split(" ");
        if (tokens.length < 1) {
            //Empty input line
            return null;
        }
        String cmdType = tokens[0];
        Message message = null;
        if (cmdType.equals("/help")) {
            userOutput.println(getHelp());
        } else if (shellCommands.containsKey(cmdType)) {
            ClientCommandHandler command = shellCommands.get(cmdType);
            try {
                message = command.handleInput(tokens, currentClient);
            } catch (ClientInputException e) {
                userOutput.println(e.getMessage());
                userOutput.println(command.getCommandHelp());
            }
        } else {
            userOutput.println("Error: Command " + cmdType + " not supported");
        }

        if (message != null && currentClient.isLoginnedFlag()) {
            message.setSenderId(currentClient.getId());
        }

        return message;
    }

}
