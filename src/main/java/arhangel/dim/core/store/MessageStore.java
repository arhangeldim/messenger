package arhangel.dim.core.store;

import java.sql.SQLException;
import java.util.Map;

import arhangel.dim.core.message.Chat;
import arhangel.dim.core.message.Message;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    void addMessage(int authorId, String from, String message, Chat chat) throws Exception;

    Map<Integer, Message> getMessagesMap() throws Exception;

    void close() throws SQLException;
}
