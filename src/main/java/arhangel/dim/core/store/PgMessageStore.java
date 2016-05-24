package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import javax.xml.soap.Text;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thefacetakt on 19.04.16.
 */
public class PgMessageStore implements MessageStore {
    QueryExecutor executor;

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        final ArrayList<ArrayList<Long>> result
                = new ArrayList<>();

        result.add(new ArrayList<>());

        Object[] args = {userId};
        try {
            executor.execQuery("SELECT CHAT_ID from CHAT_USER " +
                            "where USER_ID = ?",
                args,
                rs -> {
                    try {
                        while (rs.next()) {
                            result.get(0).add(rs.getLong("CHAT_ID"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        result.set(0, null);
                    }
                });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result.get(0);
    }

    @Override
    public Chat getChatById(Long chatId) {
        final Chat[] result = {new Chat(chatId)};
        Object[] args = {chatId};
        try {
            executor.execQuery("SELECT USER_ID from CHAT_USER" +
                            " where CHAT_ID = ?",
                args,
                rs -> {
                    try {
                        while (rs.next()) {
                            result[0].getUsers().add(rs.getLong("USER_ID"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        result[0] = null;
                    }
                });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result[0];
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        List<Long> result = new ArrayList<>();
        Object[] args = {chatId};
        final boolean[] failed = {false};
        try {
            executor.execQuery("SELECT ID from MESSAGES where CHAT_ID = ?",
                args,
                rs -> {
                    try {
                        while (rs.next()) {
                            result.add(rs.getLong("ID"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        failed[0] = true;
                    }
                });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (failed[0]) {
            return null;
        }
        return result;
    }

    @Override
    public Message getMessageById(Long messageId) {
        final TextMessage[] result = {new TextMessage()};
        result[0].setId(messageId);
        Object[] args = {messageId};
        try {
            executor.execQuery("SELECT CHAT_ID, USER_ID, CONTENT, TIME" +
                    " FROM MESSAGES " +
                    "WHERE ID = ?", args, rs -> {
                    try {
                        //if !rs.next()
                        rs.next();
                        result[0].setChatId(rs.getLong("CHAT_ID"));
                        result[0].setSenderId(rs.getLong("USER_ID"));
                        result[0].setText(rs.getString("CONTENT"));
                        result[0].setTimestamp(rs.getTimestamp("TIME")
                                .toLocalDateTime());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        result[0] = null;
                    }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result[0];
    }

    @Override
    public Message addMessage(Long chatId, Message message) {
        TextMessage tm = (TextMessage) message;
        Object[] args = {tm.getChatId(), tm.getSenderId(), tm.getText(),
                         Timestamp.valueOf(tm.getTimestamp())};
        try {
            tm.setId(executor.execUpdate(
                    "INSERT INTO MESSAGES(CHAT_ID, USER_ID, CONTENT, TIME)" +
                            "VALUES (?, ?, ?, ?)", args));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return tm;
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        Object[] args = {chatId, userId};
        try {
            executor.execQuery("INSERT INTO CHAT_USER(CHAT_ID, USER_ID)" +
                    "VALUES (?, ?)", args, rs -> {
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long addChat() {
        Object[] args = {};
        try {
            return executor.execUpdate("INSERT INTO CHATS DEFAULT VALUES",
                    args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PgMessageStore() {
        executor = new QueryExecutor();
    }

    public static void main(String[] args) {
        PgMessageStore ms = new PgMessageStore();
        TextMessage tm = new TextMessage();
        tm.setSenderId(2L);
        tm.setChatId(1L);
        tm.setText("Yeah, Life is wonderful4");

        Message msg = ms.addMessage(1L, tm);
        System.out.println(msg.getId());
    }
}
