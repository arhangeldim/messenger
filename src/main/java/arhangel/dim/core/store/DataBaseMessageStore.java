package arhangel.dim.core.store;

import arhangel.dim.core.jdbc.QueryExecutor;
import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Message;

import java.sql.Connection;
import java.sql.SQLException;
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
        return;
    }

    @Override
    public synchronized Map<Integer, Message> getMessagesMap() throws Exception {
        return null;
    }

    @Override
    public void close() throws SQLException {
        executor.close();
    }
}
