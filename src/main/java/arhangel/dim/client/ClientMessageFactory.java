package arhangel.dim.client;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.HelpMessage;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InvalidMessageException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

public class ClientMessageFactory {
    public Message createMessage(String messageText) throws InvalidMessageException {
        String[] tokens = messageText.split(" ");
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                return createLoginMessage(tokens);
            case "/help":
                return createHelpMessage();
            case "/text":
                return  createTextMessage(tokens);
            case "/info":
                return createInfoMessage(tokens);
            case "/chat_list":
                return createChatListMessage(tokens);
            case "/chat_create":
                return createChatCreateMessage(tokens);
            case "/chat_history":
                return createChatHistoryMessage(tokens);
            default:
                throw new InvalidMessageException("Invalid message type");
        }
    }

    private Message createLoginMessage(String... tokens) throws InvalidMessageException {
        if (tokens.length != 3) {
            throw new InvalidMessageException("Invalid parametres count");
        }
        LoginMessage sendMessage = new LoginMessage(tokens[1], tokens[2]);
        return sendMessage;
    }

    private Message createHelpMessage() {
        HelpMessage sendMessage = new HelpMessage();
        return sendMessage;
    }

    private Message createTextMessage(String... tokens) throws InvalidMessageException {
        if (tokens.length < 3) {
            throw new InvalidMessageException("Invalid parametres count");
        }
        String text = "";
        for (int i = 2; i < tokens.length; i++) {
            text += tokens[i];
        }
        TextMessage sendMessage = new TextMessage(Long.parseLong(tokens[1]), text);
        return sendMessage;
    }

    private Message createInfoMessage(String... tokens) throws InvalidMessageException {
        if (tokens.length > 2) {
            throw new InvalidMessageException("Invalid parametres count");
        } else if (tokens.length == 2) {
            return new InfoMessage(Long.parseLong(tokens[1]));
        } else {
            return new InfoMessage();
        }
    }

    private Message createChatListMessage(String... tokens) {
        Message sendMessage = new ChatListMessage();
        return sendMessage;
    }

    private Message createChatCreateMessage(String... tokens) throws InvalidMessageException {
        if (tokens.length < 2) {
            throw new InvalidMessageException("Invalid parametres count");
        }
        Long[] participants = new Long[tokens.length - 1];
        for (int i = 1; i < tokens.length; i++) {
            participants[i - 1] = Long.parseLong(tokens[i]);
        }
        Message sendMessage = new ChatCreateMessage(participants);
        return sendMessage;
    }

    private Message createChatHistoryMessage(String... tokens) throws InvalidMessageException {
        if (tokens.length != 2) {
            throw new InvalidMessageException("Invalid parametres count");
        }
        Message sendMessage = new ChatHistMessage(Long.parseLong(tokens[1]));
        return sendMessage;
    }
}
