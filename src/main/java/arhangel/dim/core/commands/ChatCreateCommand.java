package arhangel.dim.core.commands;

import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.store.DataStore;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatCreateCommand implements Command {

    /**
     * args 0 - название команды, 1 - список участников чата через запятую
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        DataStore dataStore = session.getDataStore();
        int authorId = session.getCurrentUserId();
        if (authorId == -1) {
            success = AnswerMessage.Value.LOGIN;
        } else {
            if (args.length != 2) {
                success = AnswerMessage.Value.NUM_ARGS;
            } else {
                UserStore userStore = dataStore.getUserStore();
                List<Integer> participants = new ArrayList<>();
                String[] parsedArg = args[1].split(",");
                participants.add(authorId);
                for (String it : parsedArg) {
                    int id = Integer.parseInt(it);
                    participants.add(id);
                    if (userStore.getUser(id) == null) {
                        message = String.format("There is no user with id %d", id);
                        success = AnswerMessage.Value.ERROR;
                        writer.write(protocol.encode(new AnswerMessage(message, success)));
                        return;
                    }
                }
                int result = dataStore.getChatStore().createChat(participants);
                message = "Chat was successfully created, id: " + result;
                success = AnswerMessage.Value.SUCCESS;
            }
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    public String toString() {
        return "/chat_create";
    }
}
