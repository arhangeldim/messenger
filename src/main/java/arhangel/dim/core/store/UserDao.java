package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by nv on 24.05.16.
 */
public class UserDao implements UserStore {
    DaoFactory daoFactory = DaoFactory.getInstance();
    Connection connection = daoFactory.connect();

    public static void main(String[] args) {
        UserDao instance = new UserDao();
        User newUser = instance.getUser("velik97", "123");
        System.out.println(newUser);
    }

    @Override
    public User addUser(User user) {
        User newUser = null;

        try {
            connection = daoFactory.connect();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO USERS (LOGIN, PASSWORD) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();

            newUser = new User(
                    generatedKeys.getLong(1),
                    generatedKeys.getString("LOGIN"),
                    generatedKeys.getString("PASSWORD")
            );

        } catch (SQLException e) {
            e.getStackTrace();
        }
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        User newUser = null;

        try {
            connection = daoFactory.connect();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE LOGIN = ? AND PASSWORD = ?");
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();
            rs.next();

            newUser = new User();
            newUser.setName(rs.getString("LOGIN"));
            newUser.setPassword(rs.getString("PASSWORD"));
            newUser.setId(rs.getLong("ID"));

        } catch (SQLException e) {
            e.getStackTrace();
        }
        return newUser;
    }

    @Override
    public User getUser(String login, String pass) {
        User newUser = null;

        try {
            connection = daoFactory.connect();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE LOGIN = ? AND PASSWORD = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);

            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                newUser = new User();
                newUser.setName(rs.getString("login"));
                newUser.setPassword(rs.getString("password"));
                newUser.setId(rs.getLong("id"));
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM users WHERE login = ?");
                preparedStatement.setString(1, login);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    newUser = new User();
                    newUser.setName(login);
                    return newUser;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            e.getStackTrace();
        }
        return newUser;
    }

    @Override
    public User getUserById(Long id) {
        User newUser = null;

        try {
            connection = daoFactory.connect();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE ID = ?");
            preparedStatement.setLong(1, id);

            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();
            rs.next();

            newUser = new User();
            newUser.setName(rs.getString("LOGIN"));
            newUser.setPassword(rs.getString("PASSWORD"));
            newUser.setId(rs.getLong("ID"));

        } catch (SQLException e) {
            System.err.println(e.getStackTrace().toString());;
        }
        return newUser;
    }
}
