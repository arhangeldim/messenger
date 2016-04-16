package arhangel.dim.client.commands;

import arhangel.dim.client.ClientInputException;
import arhangel.dim.client.ClientUser;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.Message;

import java.util.LinkedList;
import java.util.List;

public class ChatCreateCommand extends ClientCommandHandler {
    public ChatCreateCommand(String name) {
        super(name);
    }

    private List<Long> getChatParticipants(String[] input) throws ClientInputException {
        List<Long> participants = new LinkedList<>();

        try {
            if (input.length == 2) {
                String[] strUsers = input[1].split(",");
                for (String strUser : strUsers) {
                    participants.add(Long.parseUnsignedLong(strUser));
                }
            } else {
                for (int i = 1; i < input.length; ++i) {
                    participants.add(Long.parseUnsignedLong(input[i]));
                }
            }
        } catch (Exception e) {
            throw new ClientInputException("Cannot parse userlist");
        }

        return participants;
    }

    @Override
    public Message handleInput(String[] input, ClientUser user) throws ClientInputException {
        this.checkUserLogin(user);

        if (input.length < 2) {
            throw new ClientInputException("Specify at least one user");
        }
        List<Long> participants = getChatParticipants(input);
        participants.add(user.getId());

        return new ChatCreateMessage(participants);
    }
}
