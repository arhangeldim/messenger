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
            PreparedStatement preparedStatement = conn.prepareStatement("insert into User(login, password) values(?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.execute();

            log.trace("Getting result set");
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();

            log.trace("Creating found customer to return");
            newuser = new User(resultSet.getString("login"), resultSet.getString("password"));
            newuser.setId(Long.parseLong(resultSet.getString("id")));
            log.info("Customer with login=" + newuser.getLogin() + " created");

            resultSet.close();
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
            log.info("Getting user with login=" + login);
            log.trace("Opening connection");
            conn = daoFactory.connect();

            log.trace("Creating prepared statement");
            PreparedStatement preparedStatement = conn.prepareStatement("select id, login, password from User where login = ? and password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            preparedStatement.execute();

            log.trace("Get result set");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                founduser = new User(resultSet.getString("login"), resultSet.getString("password"));
                founduser.setId(Long.parseLong(resultSet.getString("id")));
                log.info("Customer with login=" + founduser.getLogin() + " found");
            } else {
                log.trace("Such user wasn't found");
            }

            resultSet.close();
            log.trace("result set closed");

            preparedStatement.close();
            log.trace("prepared statement closed");

            conn.close();
            log.trace("Connection closed");
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
