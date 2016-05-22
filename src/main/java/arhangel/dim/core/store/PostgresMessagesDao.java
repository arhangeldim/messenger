package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.dao.AbstractJDBCDao;
import arhangel.dim.core.store.dao.MessageDao;
import arhangel.dim.core.store.dao.PersistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olegchuikin on 01/05/16.
 */
public class PostgresMessagesDao extends AbstractJDBCDao<TextMessage, Long> implements MessageDao {

    public static final String ID_ = "id";
    public static final String ADMIN_ = "admin_id";
    public static final String TEXT_ = "text";
    public static final String CHAT_ = "chat_id";
    public static final String TIMESTAMP_ = "timestamp";

    public PostgresMessagesDao(Connection connection) {
        super(connection);

        TABLE_NAME = "messages";

        //todo where should it be
        Statement statement = null;
        try {

            String sql;
//            statement = connection.createStatement();
//            sql = "DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME + ";";
//            statement.executeUpdate(sql);

            statement = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                    "(id SERIAL PRIMARY KEY, " +
                    " text VARCHAR(1023), " +
                    " chat_id BIGINT, " +
                    " timestamp BIGINT, " +
                    " admin_id BIGINT)";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSelectQuery() {
        return String.format("SELECT %s, %s, %s, %s, %s FROM %s ", ID_, TEXT_, CHAT_, TIMESTAMP_, ADMIN_, TABLE_NAME);
    }

    @Override
    public String getCreateQuery() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s) \nVALUES (?, ?, ?, ?);",
                TABLE_NAME, TEXT_, CHAT_, TIMESTAMP_, ADMIN_);
    }

    @Override
    public String getUpdateQuery() {
        return String.format("UPDATE %s \n" +
                        "SET %s = ?, %s = ?, %s = ?, %s = ? \n" +
                        "WHERE %s = ?;",
                TABLE_NAME, TEXT_, CHAT_, TIMESTAMP_, ADMIN_, ID_);
    }

    @Override
    public String getDeleteQuery() {
        return String.format("DELETE FROM %s WHERE %s = ?;", TABLE_NAME, ID_);
    }

    @Override
    protected List<TextMessage> parseResultSet(ResultSet rs) throws PersistException {
        List<TextMessage> result = new ArrayList<>();
        try {
            while (rs.next()) {
                TextMessage msg = new TextMessage();
                msg.setId(rs.getLong(ID_));
                msg.setText(rs.getString(TEXT_));
                msg.setChatId(rs.getLong(CHAT_));
                msg.setSenderId(rs.getLong(ADMIN_));
                msg.setTimestamp(rs.getLong(TIMESTAMP_));
                msg.setType(Type.MSG_TEXT);
                result.add(msg);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, TextMessage msg) throws PersistException {
        try {
            statement.setString(1, msg.getText());
            statement.setLong(2, msg.getChatId());
            statement.setLong(3, msg.getTimestamp());
            statement.setLong(4, msg.getSenderId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, TextMessage msg) throws PersistException {
        try {
            statement.setString(1, msg.getText());
            statement.setLong(2, msg.getChatId());
            statement.setLong(3, msg.getTimestamp());
            statement.setLong(4, msg.getSenderId());
            statement.setLong(5, msg.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public TextMessage create() throws PersistException {
        TextMessage msg = new TextMessage();
        msg.setType(Type.MSG_TEXT);
        return persist(msg);
    }

    @Override
    public List<TextMessage> getMessagesWithChatId(Long chatId) throws PersistException {
        return getByLongFieldValue(CHAT_, chatId);
    }
}
