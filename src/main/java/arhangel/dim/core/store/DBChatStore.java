package arhangel.dim.core.store;

import arhangel.dim.core.jdbc.QueryExecutor;
import arhangel.dim.core.message.Chat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Реализация хранилища чатов в БД PostegreSql
 */
public class DBChatStore implements ChatStore {
    private Connection connection;
    private QueryExecutor executor;

    public DBChatStore(Connection conn) {
        this.connection = conn;
        this.executor = new QueryExecutor();
    }

    @Override
    public synchronized int createChat(List<Integer> participants) throws Exception {
        int chatId = -1;
        String sql = "INSERT INTO chats (temp) VALUES (?)";
        Map<Integer, Object> queryArgs = new HashMap<>();
        queryArgs.put(1, "temp");
        executor.prepareStatementGeneratedKeys(connection, sql);
        chatId = executor.execUpdate(sql, queryArgs, (r) -> {
            if (r.next()) {
                return r.getInt(1);
            }
            return -1;
        });
        sql = "INSERT INTO userschat (user_id, chat_id) VALUES (?, ?)";
        executor.prepareStatement(connection, sql);
        for (Integer userId : participants) {
            queryArgs.clear();
            queryArgs.put(1, userId);
            queryArgs.put(2, chatId);
            executor.execUpdate(sql, queryArgs);
        }
        return chatId;
    }

    @Override
    public Map<Integer, Chat> getChatList() throws Exception {
        String sql = "SELECT * FROM userschat";
        return executor.execQuery(connection, sql, (r) -> {
            Map<Integer, Chat> result = new HashMap<>();
            while (r.next()) {
                int chatId = r.getInt("chat_id");
                int userId = r.getInt("user_id");
                Chat chat = result.get(chatId);
                if (chat == null) {
                    chat = new Chat(chatId, connection);
                    chat.addParticipant(userId);
                    result.put(chatId, chat);
                } else {
                    chat.addParticipant(userId);
                }
            }
            return result;
        });
    }

    @Override
    public Chat getChat(Integer id) throws Exception {
        Chat result = new Chat(id, connection);
        String sql = "SELECT * FROM userschat WHERE chat_id = ?";
        Map<Integer, Object> queryArgs = new HashMap<>();
        queryArgs.put(1, id);
        executor.prepareStatement(connection, sql);
        executor.execQuery(sql, queryArgs, (r) -> {
            while (r.next()) {
                int userId = r.getInt("user_id");
                result.addParticipant(userId);
            }
            return null;
        });
        return result;
    }

    @Override
    public void close() throws SQLException {
        executor.close();
    }
}
