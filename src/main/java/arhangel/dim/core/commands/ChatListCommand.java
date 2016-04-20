package arhangel.dim.core.commands;

import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.store.DataStore;
import arhangel.dim.core.session.Session;

import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

public class ChatListCommand implements Command {

    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        AnswerMessage.Value success;
        String message = "";
        DataStore dataStore = session.getDataStore();
        if (session.getCurrentUserId() == -1) {
            success = AnswerMessage.Value.LOGIN;
        } else {
            success = AnswerMessage.Value.SUCCESS;
            Map<Integer, Chat> result = dataStore.getChatStore().getChatList();
            StringBuilder builder = new StringBuilder();
            builder.append("Chat list:\n");
            for (Map.Entry<Integer, Chat> it : result.entrySet()) {
                builder.append("Chat ");
                builder.append(it.getKey());
                builder.append(", users: ");
                List<Integer> participantsId = it.getValue().getParticipantIds();
                for (Integer userIdIt : participantsId) {
                    builder.append(userIdIt);
                    builder.append(", ");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append("\n");
            }
            builder.deleteCharAt(builder.length() - 1);
            message = builder.toString();
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    public String toString() {
        return "/chat_list";
    }
}
