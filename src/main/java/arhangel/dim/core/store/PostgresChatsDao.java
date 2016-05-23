package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.store.dao.BaseJdbcDao;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.DaoFactory;
import arhangel.dim.core.store.dao.GenericDao;
import arhangel.dim.core.store.dao.PersistException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olegchuikin on 01/05/16.
 */
public class PostgresChatsDao extends BaseJdbcDao<Chat, Long> implements ChatDao {

    private DaoFactory daoFactory;

    public static final String ID_ = "id";
    public static final String ADMIN_ = "admin_id";

    private static final String TABLE_NAME_CHAT_USERS = "chats_users";
    private static final String CHAT_ID = "chat_id";
    private static final String USER_ID = "user_id";

    public PostgresChatsDao(Connection connection, DaoFactory daoFactory) {
        super(connection);
        this.daoFactory = daoFactory;
        tableName = "chats";

        //todo where should it be
        Statement statement = null;
        try {
//            clearTables();

            String sql;

            statement = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " " +
                    "(id SERIAL PRIMARY KEY, " +
                    " admin_id BIGINT)";
            statement.executeUpdate(sql);

            statement = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CHAT_USERS + " " +
                    "(chat_id BIGINT, " +
                    " user_id BIGINT)";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearTables() throws PersistException {
        super.clearTables();
        try {
            Statement statement = connection.createStatement();
            String sql = "DROP TABLE IF EXISTS " + TABLE_NAME_CHAT_USERS + ";";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new PersistException(e);
        }
    }

    @Override
    public Chat persist(Chat chat) throws PersistException {
        List<Long> participants = chat.getParticipants();
        chat = super.persist(chat);
        chat.setParticipants(participants);
        addParticipantsToTable(chat);
        return chat;
    }

    @Override
    public Chat getByPk(Long key) throws PersistException {
        Chat chat = super.getByPk(key);
        GenericDao<User, Long> userDao = daoFactory.getDao(User.class);

        chat.setParticipants(getParticipantsOfChatWithId(chat.getId()));
        return chat;
    }

    @Override
    public String getSelectQuery() {
        return String.format("SELECT %s, %s FROM %s ", ID_, ADMIN_, tableName);
    }

    @Override
    public String getCreateQuery() {
        return String.format("INSERT INTO %s (%s) \nVALUES (?);", tableName, ADMIN_);
    }

    @Override
    public String getUpdateQuery() {
        return String.format("UPDATE %s \n" +
                        "SET %s = ? \n" +
                        "WHERE %s = ?;",
                tableName, ADMIN_, ID_);
    }

    @Override
    public String getDeleteQuery() {
        return String.format("DELETE FROM %s WHERE %s = ?;", tableName, ID_);
    }

    @Override
    protected List<Chat> parseResultSet(ResultSet rs) throws PersistException {
        List<Chat> result = new ArrayList<>();
        try {
            while (rs.next()) {
                Chat chat = new Chat();
                chat.setId(rs.getLong(ID_));
                GenericDao<User, Long> userDao = daoFactory.getDao(User.class);
                chat.setAdmin(userDao.getByPk(rs.getLong(ADMIN_)));

                List<Long> participants = getParticipantsOfChatWithId(chat.getId());
                chat.setParticipants(participants);
                result.add(chat);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Chat chat) throws PersistException {
        try {
            statement.setLong(1, chat.getAdmin().getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Chat chat) throws PersistException {
        try {
            statement.setLong(1, chat.getAdmin().getId());
            statement.setLong(2, chat.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public Chat create() throws PersistException {
        Chat chat = new Chat();
        return persist(chat);
    }

    private List<Long> getParticipantsOfChatWithId(Long chatId) throws PersistException {
        String sql = "SELECT user_id FROM chats_users WHERE chat_id = ?";

        List<Long> result = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(rs.getLong("user_id"));
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    private List<Long> getChatsOfUserWithId(Long userId) throws PersistException {
        String sql = "SELECT chat_id FROM chats_users WHERE user_id = ?";

        List<Long> result = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                result.add(rs.getLong("chat_id"));
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    private void addParticipantsToTable(Chat chat) throws PersistException {
        String sql = "INSERT INTO chats_users (chat_id, user_id) \nVALUES (?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Long userId : chat.getParticipants()) {
                statement.setLong(1, chat.getId());
                statement.setLong(2, userId);

                int count = statement.executeUpdate();
                if (count != 1) {
                    throw new PersistException("On persist modify more then 1 record: " + count);
                }
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public List<Chat> getChatsByAdmin(User admin) throws PersistException {
        List<Long> chatsId = getChatsOfUserWithId(admin.getId());
        List<Chat> chats = new ArrayList<>();
        for (Long chatId : chatsId) {
            chats.add(getByPk(chatId));
        }
        return chats;
    }
}
