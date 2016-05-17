package arhangel.dim.core.store;

import arhangel.dim.core.jdbc.QueryExecutor;
import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Message;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация хранилища сообщений на БД PostegreSQL
 */
public class DataBaseMessageStore implements MessageStore {
    private Connection connection;
    private int chatId;
    private QueryExecutor executor;

    public DataBaseMessageStore(Connection conn, Chat chat) {
        this.connection = conn;
        this.chatId = chat.getId();
        this.executor = new QueryExecutor();
    }

    @Override
    public synchronized void addMessage(int authorId, String authorName, String value, Chat chat) throws Exception {
        try {
            if (value.contains("\'")) {
                value = value.replace('\'', ' ');
            }
            String sql = "INSERT INTO message (author_id, value, chat_id) VALUES (? ,? ,?)";
            Map<Integer, Object> queryArgs = new HashMap<>();
            queryArgs.put(1, authorId);
            queryArgs.put(2, value);
            queryArgs.put(3, chat.getId());
            executor.prepareStatement(connection, sql);
            executor.execUpdate(sql, queryArgs);
        } catch (Exception e) {
            System.err.println("MessageStore: failed to write data " + e.getMessage());
        }
    }

    @Override
    public synchronized Map<Integer, Message> getMessagesMap() throws Exception {
        Map<Integer, Message> messageMap = new HashMap<>();
        String sql = "SELECT * FROM message WHERE chat_id = ? LIMIT 10000";
        Map<Integer, Object> queryArgs = new HashMap<>();
        queryArgs.put(1, chatId);
        executor.prepareStatement(connection, sql);
        executor.execQuery(sql, queryArgs, (r) -> {
            while (r.next()) {
                int authorId = r.getInt("author_id");
                String value = r.getString("value");
                int messageId = r.getInt("id");
                Message message = new Message();
                message.setAuthorId(authorId);
                message.setMessage(value);
                message.setId(messageId);
                messageMap.put(messageId, message);
            }
            return null;
        });
        return messageMap;
    }

    @Override
    public void close() throws SQLException {
        executor.close();
    }
}
