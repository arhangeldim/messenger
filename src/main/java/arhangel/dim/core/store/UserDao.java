package arhangel.dim.core.store;

import arhangel.dim.core.User;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;


/**
 * Created by philip on 13.04.16.
 */
public class UserDao implements UserStore {
    private static Logger log = Logger.getLogger(UserDao.class.getName());
    DaoFactory daoFactory =  DaoFactory.getInstance();

    @Override
    public User addUser(User user) {

        User newuser = null;
        try {
            log.info("Creating new customer with login=" + user.getLogin());
            log.trace("Opening connection");
            Connection conn = daoFactory.connect();

            log.trace("Creating prepared statement");
            PreparedStatement preparedStatement = conn.prepareStatement("insert into Usertable (login, pwd) values(?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());

            int affectedRows = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();

            newuser = new User(generatedKeys.getString("login"), generatedKeys.getString("pwd"));
            newuser.setId(generatedKeys.getLong(1));

            // Добавление нового пользователя в базовый чат
            preparedStatement = conn.prepareStatement("insert into user_chat (user_id, chat_id) values(?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, newuser.getId());
            preparedStatement.setLong(2, 1);

            affectedRows = preparedStatement.executeUpdate();

            generatedKeys.close();
            log.trace("result set closed");

            preparedStatement.close();
            log.trace("prepared statement closed");

            conn.close();
            log.trace("Connection closed");
        } catch (SQLException e) {
            e.getMessage();
        }
        return newuser;
    }

    @Override
    public User getUser(String login, String pass) {

        Connection conn =  daoFactory.connect();
        User founduser = null;

        try {
            conn = daoFactory.connect();

            PreparedStatement preparedStatement = conn.prepareStatement("select id, login, pwd from Usertable where login = ? and pwd = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                founduser = new User(resultSet.getString("login"), resultSet.getString("pwd"));
                founduser.setId(Long.parseLong(resultSet.getString("id")));
            } else {
                preparedStatement = conn.prepareStatement("select id, login, pwd from Usertable where login = ?");
                preparedStatement.setString(1, login);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return  founduser = new User(resultSet.getString("login"), null);
                } else {
                    return null;
                }
            }

            resultSet.close();

            preparedStatement.close();

            conn.close();
        } catch (SQLException e) {
            e.getMessage();
        }
        return founduser;
    }

    public User getUserById(Long id) {
        return null;
    }

    public User updateUser(User user) {
        return null;
    }
}
