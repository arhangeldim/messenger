package arhangel.dim.core.dbservice.dao;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.dbservice.executor.QueryExecutor;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersDao {
    static org.slf4j.Logger log = LoggerFactory.getLogger(UsersDao.class);
    QueryExecutor queryExecutor;

    public UsersDao() {
    }

    // database access test
    public static void main(String[] args) throws Exception {
        UsersDao usersDao = new UsersDao();
        usersDao.init();

        //Long num = new Long(0);
        usersDao.createTables();
        //usersDao.addUserToChat(new Long(0),new Long(1));
        usersDao.addUser("spec45as", "alphatest");
        usersDao.addUser("spec45as2", "alphatest2");

    }

    public void init() throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("jdbc:postgresql");
        source.setServerName("178.62.140.149");

        //TODO: актуальный когда допилишь для проекта
        source.setDatabaseName("spec45as");
        source.setUser("trackuser");
        source.setPassword("trackuser");
        source.setMaxConnections(10);

        Connection connection = source.getConnection();

        queryExecutor = new QueryExecutor();
        queryExecutor.setConnection(connection);
        createTables();

    }

    public void createTables() throws SQLException {
        try {
            createUsers();
            createChats();
            createMessages();
        } catch (SQLException e) {
            throw new SQLException("Ошибка создания таблиц", e);
        }
    }

    public void createUsers() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                " user_id SERIAL PRIMARY KEY, " +
                " user_login character varying, " +
                " user_pass character varying " +
                ");";
        queryExecutor.updateQuery(sql);
    }

    public void createMessages() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                " msg_id SERIAL PRIMARY KEY, " +
                " msg_text character varying, " +
                " chat_id integer, " +
                " timestamp timestamp, " +
                " user_id integer " +
                ");";
        queryExecutor.updateQuery(sql);
    }

    public void createChats() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS chats (" +
                " user_id bigint, " +
                " chat_id bigint " +
                ");";
        queryExecutor.updateQuery(sql);
    }


    public User getUser(String userName) throws Exception {

        String sql = "SELECT user_id, user_login, user_pass " +
                " FROM users " +
                " WHERE user_login = ? ";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, userName);

        return queryExecutor.execQuery(sql, prepared, resultSet -> {
            if (resultSet.next()) {
                String login = resultSet.getString("user_login");
                String hash = resultSet.getString("user_pass");
                Long id = resultSet.getLong("user_id");
                User user1 = new User(login);
                user1.setPass(hash);
                user1.setId(id);
                return user1;
            }
            return null;
        });

    }

    public User getUserById(Long id) throws Exception {
        String sql = "SELECT user_login, user_pass " +
                " FROM users " +
                " WHERE user_id = ? ";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, id);

        User user = queryExecutor.execQuery(sql, prepared, resultSet -> {
            if (resultSet.next()) {
                String login = resultSet.getString("user_login");
                String hash = resultSet.getString("user_pass");
                User user1 = new User(login);
                user1.setHash(hash);
                return user1;
            }
            return null;
        });
        if (user != null) {
            user.setId(id);
        }
        return user;
    }

    public void setNewPass(String login, String password) throws Exception {

        String sqlUpdate = "UPDATE users " +
                " SET user_pass = ? " +
                " WHERE user_login = ? ;";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, login);
        prepared.put(2, password);

        queryExecutor.updateQuery(sqlUpdate, prepared);
    }

    public void addChat(Chat chat) {

        String sqlInsert = "INSERT INTO chats (chat_id) VALUES " +
                "(?) ;";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chat.getCreatorId());

        Long key = null;
        try {
            key = queryExecutor.updateQueryWithGeneratedKey(sqlInsert, prepared, "chat_id");
            log.info("key = " + key);
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sqlInsert + ", where ?=" + chat.getCreatorId(), sqlExc);
        }

        chat.setId(key);

    }

    public void addUserToChat(Long userId, Long chatId) {

        String sqlInsert = "INSERT INTO chats (\"chat_id\", \"user_id\") " +
                "VALUES " +
                "(?, ?); ";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        prepared.put(2, userId);

        try {
            queryExecutor.updateQuery(sqlInsert, prepared);
        } catch (SQLException sqlExc) {
            log.error("Ошибка запроса:\n" + sqlInsert + "\n" + "where ?=" + chatId + ", ?=" + userId);
            log.error(String.valueOf(sqlExc));
        }
    }

    public List<Long> getChatsByUserId(Long userId) {

        String sql = "SELECT chat_id " +
                "FROM chats " +
                "WHERE user_id = ?";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, userId);

        List<Long> chatList = null;
        try {
            chatList = queryExecutor.execQuery(sql, prepared, resultSet -> {
                List<Long> chatList1 = new ArrayList<>();
                while (resultSet.next()) {
                    chatList1.add(resultSet.getLong("chat_id"));
                }
                return chatList1;
            });
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sql + "?=" + userId, sqlExc);
        }
        return chatList;
    }

    public List<Long> getUsersByChatId(Long chatId) {
        String sql = "SELECT user_id " +
                "FROM chats " +
                "WHERE chat_id = ?";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);

        List<Long> userList = null;
        try {
            userList = queryExecutor.execQuery(sql, prepared, resultSet -> {
                List<Long> userList1 = new ArrayList<>();
                while (resultSet.next()) {
                    userList1.add(resultSet.getLong("user_id"));
                }
                return userList1;
            });
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sql + "?=" + chatId, sqlExc);
        }
        return userList;
    }

    public void addMessage(Long chatId, Message msg) {

        String sqlInsert = "INSERT INTO messages " +
                "(msg_text, chat_id, timestamp, user_id) VALUES " +
                "(?, ?, ?, ?) ";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, msg.getMessage());
        prepared.put(2, chatId);
        prepared.put(3, msg.getTime());
        prepared.put(4, msg.getSenderId());

        try {
            Long id = queryExecutor.updateQueryWithGeneratedKey(sqlInsert, prepared, "msg_id");
            msg.setId(id);
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sqlInsert +
                            prepared.get(1) +
                            prepared.get(2) +
                            prepared.get(3) +
                            prepared.get(4),
                    sqlExc);
        }
    }

    public List<Long> getMessagesByChatId(Long chatId) {

        String sql = "SELECT msg_id " +
                "FROM messages " +
                "WHERE chat_id = ?";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);

        List<Long> msgList = null;
        try {
            msgList = queryExecutor.execQuery(sql, prepared, resultSet -> {
                List<Long> msgList1 = new ArrayList<>();
                while (resultSet.next()) {
                    msgList1.add(resultSet.getLong("msg_id"));
                }
                return msgList1;
            });
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sql + "?=" + chatId, sqlExc);
        }
        return msgList;

    }

    public Message getMessageById(Long messageId) {

        String sql = "" +
                "SELECT * " +
                "FROM messages " +
                "WHERE msg_id = ?";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, messageId);
        Message msg = null;
        try {
            msg = queryExecutor.execQuery(sql, prepared, resultSet -> {
                Message msg1 = new Message();
                if (resultSet.next()) {
                    msg1.setId(resultSet.getLong("msg_id"));
                    msg1.setMessage(resultSet.getString("msg_text"));
                    msg1.setType(Type.MSG_TEXT);
                    msg1.setSenderId(resultSet.getLong("user_id"));
                    msg1.setTime(resultSet.getString("timestamp"));
                    return msg1;
                }
                return null;
            });
        } catch (SQLException sqlExc) {
            log.error("Проблемы с sql запросом: " + sql + "where ?=" + messageId, sqlExc);
        }
        return msg;
    }

    public User addUser(String userName, String password) throws Exception {

        User user;

        String sqlInsert = "INSERT INTO users (\"user_login\", \"user_pass\") VAlUES " +
                "(?, ?);";

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, userName);
        prepared.put(2, password);

        Long id = queryExecutor.updateQueryWithGeneratedKey(sqlInsert, prepared, "user_id");

        user = new User(userName);
        user.setHash(password);
        user.setId(id);
        return user;

    }

    public void close() {
        queryExecutor.close();
    }
}
