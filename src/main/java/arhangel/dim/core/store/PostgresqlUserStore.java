package arhangel.dim.core.store;

import arhangel.dim.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PostgresqlUserStore implements UserStore {

    private Connection connection;

    static Logger log = LoggerFactory.getLogger(PostgresqlUserStore.class);

    public PostgresqlUserStore(Connection connection) throws SQLException {
        this.connection = connection;

        Statement statement;
        String sql;
//        statement = connection.createStatement();
//        sql = "DROP TABLE IF EXISTS USERS;";
//        statement.executeUpdate(sql);

        statement = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS USERS " +
                "(id SERIAL PRIMARY KEY, " +
                " name VARCHAR(255), " +
                " password VARCHAR(255))";
        statement.executeUpdate(sql);
    }

    @Override
    public User addUser(User user) throws PersistException {
        if (user.getId() != null) {
            return user;
        }

        //todo see in blog how fix get/set for login

        User persistInstance;

        String sql = getCreateQuery();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            prepareStatementForInsert(statement, user);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("On persist modify more then 1 record: " + count);
            }

        } catch (SQLException | PersistException e) {
            throw new PersistException(e);
        }

        sql = getSelectQuery() + "WHERE id = (select currval('USERS_id_seq'));";


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            List<User> list = parseResultSet(rs);
            if ((list == null) || (list.size() != 1)) {
                throw new PersistException("Exception on findByPK new persist data.");
            }
            persistInstance = list.iterator().next();
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return persistInstance;
    }

    @Override
    public User updateUser(User user) throws PersistException {
        String sql = getUpdateQuery();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            prepareStatementForUpdate(statement, user);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("On update modify more then 1 record: " + count);
            }

        } catch (SQLException e) {
            throw new PersistException(e);
        }
        return user;
    }

    @Override
    public User getUser(String login, String pass) throws PersistException {
        List<User> list;
        String sql = getSelectQuery();
        sql += " WHERE name = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, pass);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }

        if (list == null || list.size() == 0) {
            return null;
        }

        if (list.size() > 1) {
            throw new PersistException("Received more than one record.");
        }

        return list.iterator().next();
    }

    @Override
    public User getUserById(Long id) throws PersistException {
        List<User> list;
        String sql = getSelectQuery();
        sql += " WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }

        if (list == null || list.size() == 0) {
            return null;
        }

        if (list.size() > 1) {
            throw new PersistException("Received more than one record.");
        }

        return list.iterator().next();
    }

    public List<User> getAll() throws PersistException {
        List<User> list;
        String sql = getSelectQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return list;
    }

    private String getSelectQuery() {
        return "SELECT id, name, password FROM USERS ";
    }

    private String getCreateQuery() {
        return "INSERT INTO USERS (name, password) \n" +
                "VALUES (?, ?);";
    }

    private String getUpdateQuery() {
        return "UPDATE USERS \n" +
                "SET name = ?, password  = ? \n" +
                "WHERE id = ?;";
    }

    private String getDeleteQuery() {
        return null;
    }

    private List<User> parseResultSet(ResultSet rs) throws PersistException {
        List<User> result = new ArrayList<>();
        try {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                result.add(user);
            }
        } catch (SQLException e) {
            throw new PersistException(e);
        }
        return result;
    }

    private void prepareStatementForInsert(PreparedStatement statement, User user) throws PersistException {
        try {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
        } catch (Exception e) {
            throw new PersistException(e);
        }

    }

    private void prepareStatementForUpdate(PreparedStatement statement, User user) throws PersistException {
        try {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setLong(3, user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
