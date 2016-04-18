package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.UserCreateMessage;

import java.util.Scanner;

public class UserCreateCommand extends ClientCommandHandler {
    private Scanner scanner;

    public UserCreateCommand(String name) {
        super(name);
        scanner = new Scanner(userInput);
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        String username;
        String password;
        if (input.length == 3) {
            username = input[1];
            password = input[2];
        } else if (input.length == 2) {
            username = input[1];
            userOutput.print("Input password: ");
            password = scanner.nextLine();
        } else if (input.length == 1) {
            userOutput.print("Input login: ");
            username = scanner.nextLine();
            userOutput.print("Input password: ");
            password = scanner.nextLine();
        } else {
            throw new ClientInputException("Too many arguments");
        }

        UserCreateMessage message = new UserCreateMessage();
        message.setUsername(username);
        message.setPassword(password);

        return message;
    }
}
