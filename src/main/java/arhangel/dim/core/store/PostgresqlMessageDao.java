package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class PostgresqlMessageDao implements MessageStore {
    @Override
    public List<Long> getChatsByUserId(Long userId) {
        return null;
    }

    @Override
    public Chat getChatById(Long chatId) {
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        return null;
    }

    @Override
    public Message getMessageById(Long messageId) {
        return null;
    }

    @Override
    public Message addMessage(Long chatId, Message message) {
        TextMessage textMessage = (TextMessage) message;
        Connection connection = PostgresqlDaoFactory.createConnection();
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS messages (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "chat_id BIGINT REFERENCES chats (id)," +
                    "sender_id bigint REFERENCES users (id)," +
                    "text TEXT" +
                    ");").execute();
            PreparedStatement createStatement = connection.prepareStatement(
                    "INSERT INTO messages (chat_id, sender_id, text) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            createStatement.setLong(1, textMessage.getChatId());
            createStatement.setLong(2, textMessage.getSenderId());
            createStatement.setString(3, textMessage.getText());

            int affectedRows = createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();

            textMessage.setId(generatedKeys.getLong(1));

            return textMessage;

        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

    }
}
