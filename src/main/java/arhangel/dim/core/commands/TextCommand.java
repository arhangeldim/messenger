package arhangel.dim.core.commands;

import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.store.ChatStore;
import arhangel.dim.core.store.DataStore;
import arhangel.dim.core.session.Session;
import arhangel.dim.core.session.SessionManager;

import java.io.DataOutputStream;
import java.util.List;

public class TextCommand implements Command {
    /**
     * args 0 - название команды, 1 - идентификатор чата, 2,3,4... - сообщение
     */
    @Override
    public void run(String[] args, Session session) throws Exception {
        DataOutputStream writer = session.getWriter();
        Protocol<AnswerMessage> protocol = new SerializationProtocol<>();
        String message = "";
        AnswerMessage.Value success;
        DataStore dataStore = session.getDataStore();
        int authorId = session.getCurrentUserId();
        String authorName = session.getCurrentUserName();
        if (authorId == -1) {
            success = AnswerMessage.Value.LOGIN;
            writer.write(protocol.encode(new AnswerMessage(message, success)));
        } else {
            if (args.length < 3) {
                success = AnswerMessage.Value.NUM_ARGS;
                writer.write(protocol.encode(new AnswerMessage(message, success)));
                return;
            }
            int chatId = Integer.parseInt(args[1]);
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; ++i) {
                builder.append(args[i] + " ");
            }
            String messageFromUser = builder.toString();
            ChatStore chatStore = dataStore.getChatStore();
            Chat chat = chatStore.getChat(chatId);
            if (chat == null) {
                message = "Chat not found";
                success = AnswerMessage.Value.ERROR;
                writer.write(protocol.encode(new AnswerMessage(message, success)));
                return;
            }
            List<Integer> participants = chat.getParticipantIds();
            if (!participants.contains(authorId)) {
                message = "You are not allowed to write in this chat";
                success = AnswerMessage.Value.ERROR;
                writer.write(protocol.encode(new AnswerMessage(message, success)));
                return;
            }
            chat.addMessage(authorId, authorName, messageFromUser);
            SessionManager manager = session.getSessionManager();
            for (Integer id : participants) {
                success = AnswerMessage.Value.CHAT;
                Session destinationSession = manager.getSessionById(id);
                AnswerMessage answer = new AnswerMessage(messageFromUser, success);
                answer.setMessage(String.format("Chat %d, from %d: %s", chatId, authorId, messageFromUser));
                answer.setId(authorId);
                DataOutputStream destinationWriter = destinationSession.getWriter();
                destinationWriter.write(protocol.encode(answer));
            }
        }
    }

    public String toString() {
        return "/text";
    }
}
