package arhangel.dim.core.store;

import arhangel.dim.core.message.Chat;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ChatStore {

    int createChat(List<Integer> participants) throws Exception;

    Map<Integer, Chat> getChatList() throws Exception;

    Chat getChat(Integer id) throws Exception;

    void close() throws SQLException;
}
