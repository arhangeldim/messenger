package arhangel.dim.core.commands;

import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.message.Message;
import arhangel.dim.core.session.Session;
import arhangel.dim.core.store.ChatStore;

import java.io.DataOutputStream;
import java.util.Map;

public class ChatHistoryCommand implements Command {
    /**
     * args 0 - название команды, 1 - идентификатор чата
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        if (session.getCurrentUserName() == null) {
            success = AnswerMessage.Value.LOGIN;
            writer.write(protocol.encode(new AnswerMessage(message, success)));
            return;
        }
        if (args.length == 2) {
            int chatId = Integer.parseInt(args[1]);
            ChatStore chatStore = session.getDataStore().getChatStore();
            Chat chat = chatStore.getChat(chatId);
            if (chat == null) {
                message = "Chat " + chatId + " not found";
                success = AnswerMessage.Value.ERROR;
                writer.write(protocol.encode(new AnswerMessage(message, success)));
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            Map<Integer, Message> messageMap = chat.getMessageMap();
            for (Map.Entry<Integer, Message> pair : messageMap.entrySet()) {
                stringBuilder.append(pair.getValue().toString());
                stringBuilder.append("\n");
            }
            message = stringBuilder.toString();
            success = AnswerMessage.Value.SUCCESS;
        } else {
            success = AnswerMessage.Value.NUM_ARGS;
        }
        writer.write(protocol.encode(new AnswerMessage(message, success)));
    }

    @Override
    public String toString() {
        return "/chat_history";
    }
}
