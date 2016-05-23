package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.store.dao.AbstractJdbcDao;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.core.store.dao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class PostgresUserDao extends AbstractJdbcDao<User, Long> implements UserDao {

    private static final String ID_ = "id";
    private static final String NAME_ = "name";
    private static final String PASSWORD_ = "password";

    public PostgresUserDao(Connection connection) {
        super(connection);

        tableName = "USERS";

        //todo where should it be
        Statement statement = null;
        try {
            String sql;
//            statement = connection.createStatement();
//            sql = "DROP TABLE IF EXISTS " + tableName + ";";
//            statement.executeUpdate(sql);

            statement = connection.createStatement();

            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " " +
                    "(id SERIAL PRIMARY KEY, " +
                    " name VARCHAR(255), " +
                    " password VARCHAR(255))";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSelectQuery() {
        return String.format("SELECT %s, %s, %s FROM %s ", ID_, NAME_, PASSWORD_, tableName);
    }

    @Override
    public String getCreateQuery() {
        return String.format("INSERT INTO %s (%s, %s) \nVALUES (?, ?);", tableName, NAME_, PASSWORD_);
    }

    @Override
    public String getUpdateQuery() {
        return String.format("UPDATE %s \n" +
                        "SET %s = ?, %s = ? \n" +
                        "WHERE %s = ?;",
                tableName, NAME_, PASSWORD_, ID_);
    }

    @Override
    public String getDeleteQuery() {
        return String.format("DELETE FROM %s WHERE %s = ?;", tableName, ID_);
    }

    @Override
    protected List<User> parseResultSet(ResultSet rs) throws PersistException {
        List<User> result = new ArrayList<>();
        try {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong(ID_));
                user.setName(rs.getString(NAME_));
                user.setPassword(rs.getString(PASSWORD_));
                result.add(user);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, User user) throws PersistException {
        try {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, User user) throws PersistException {
        try {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setLong(3, user.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public User create() throws PersistException {
        User user = new User();
        return persist(user);
    }

    @Override
    public User getUserByLogin(String login) throws PersistException {
        List<User> users = getByStringFieldValue(NAME_, login);
        if (users == null) {
            return null;
        }
        if (users.size() > 1) {
            throw new PersistException("There are more than one user with such name");
        }
        return users.get(0);
    }
}
