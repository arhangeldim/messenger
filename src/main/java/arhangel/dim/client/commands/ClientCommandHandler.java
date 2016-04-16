package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.Message;

import java.io.InputStream;
import java.io.PrintStream;

public abstract class ClientCommandHandler {

    private String commandName;

    PrintStream userOutput;
    InputStream userInput;

    public ClientCommandHandler(String name, InputStream input, PrintStream output) {
        commandName = name;
        userInput = input;
        userOutput = output;
    }

    ClientCommandHandler(String name) {
        this(name, System.in, System.out);
    }


    /**
     * Create an appropriate message from input
     */
    public abstract Message handleInput(String[] input, ClientUser user) throws ClientInputException;

    void checkUserLogin(ClientUser user) throws ClientInputException {
        if (!user.isLoginnedFlag()) {
            throw new ClientInputException("Log in first");
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandHelp() {
        return commandName + ": No description\n";
    }
}
