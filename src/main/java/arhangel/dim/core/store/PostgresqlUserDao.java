package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgresqlUserDao implements UserStore {
    @Override
    public User addUser(User user) {
        Connection connection = PostgresqlDaoFactory.createConnection();
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "login VARCHAR(255) UNIQUE," +
                    "secret VARCHAR(255)" +
                    ");").execute();
            PreparedStatement createStatement = connection.prepareStatement(
                    "INSERT INTO users (login, secret) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            createStatement.setString(1, user.getLogin());
            createStatement.setString(2, user.getSecret());

            int affectedRows = createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();

            user.setId(generatedKeys.getLong(1));

            return user;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(String login, String pass) {
        Connection connection = PostgresqlDaoFactory.createConnection();
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "login VARCHAR(255) UNIQUE," +
                    "secret VARCHAR(255)" +
                    ");").execute();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE login=(?) AND secret=(?);");
            selectStatement.setString(1, login);
            selectStatement.setString(2, pass);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));
                return user;
            }
            return null;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {
        Connection connection = PostgresqlDaoFactory.createConnection();
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "login VARCHAR(255) UNIQUE," +
                    "secret VARCHAR(255)" +
                    ");").execute();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=(?);");
            selectStatement.setLong(1, id);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));
                return user;
            }
            return null;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public User getUserByLogin(String login) {
        Connection connection = PostgresqlDaoFactory.createConnection();
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "login VARCHAR(255) UNIQUE," +
                    "secret VARCHAR(255)" +
                    ");").execute();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE login=(?);");
            selectStatement.setString(1, login);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));
                return user;
            }
            return null;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
