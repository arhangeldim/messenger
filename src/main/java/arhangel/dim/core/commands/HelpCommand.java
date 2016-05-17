package arhangel.dim.core.commands;

import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;

public class HelpCommand implements Command {

    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "List of commands:\n" +
                "  /register <login> <password> - sign up a user\n" +
                "  /login <login> <password> - sign in with these parameters\n" +
                "  /info - get the information about you\n" +
                "  /info <nick> - get the information about \'nick\'\n" +
                "  /chat_list - list of all chats\n" +
                "  /chat_create <user_id list> - create chat with participants in list\n" +
                "  /chat_history <chat_id> - show messages in \'id\' chat\n" +
                "  /text <id> <message> send message in 'id' chat\n" +
                "  /exit - exit the program\n" +
                "  /help - show the list of commands";
        writer.write(protocol.encode(new AnswerMessage(message, AnswerMessage.Value.SUCCESS)));
    }

    public String toString() {
        return "/help";
    }
}
