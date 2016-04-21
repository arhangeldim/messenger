package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class PostgresqlMessageStore implements MessageStore {

    private DaoFactory factory;
    private Connection connection;

    private static long key = 1;

    public PostgresqlMessageStore(DaoFactory factory, Connection connection) {
        this.factory = factory;
        this.connection = connection;
    }

    private static List<TextMessage> messages = new ArrayList<>();
    private static List<Chat> chats = new ArrayList<>();

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        List<Long> result = new ArrayList<>();
        for (Chat chat : chats) {
            if (chat.getParticipants().contains(userId) || chat.getAdmin().getId().equals(userId)) {
                result.add(chat.getId());
            }
        }
        return result;
    }

    @Override
    public Chat getChatById(Long chatId) {
        for (Chat chat : chats) {
            if (chat.getId().equals(chatId)) {
                return chat;
            }
        }
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        return getChatById(chatId).getMessages();
    }

    @Override
    public Message getMessageById(Long messageId) {
        for (TextMessage message : messages) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public void addMessage(Long chatId, Message message) {
        message.setId(key++);
        getChatById(chatId).addMessage((TextMessage) message);
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        getChatById(chatId).addParticipant(userId);
    }

    @Override
    public void addChat(Chat chat) {
        chat.setId(key++);
        chats.add(chat);
    }
}
