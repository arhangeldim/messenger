package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.TextMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class SUserStore implements UserStore {
    QueryExecutor qex = new QueryExecutor();
    /**
     * Добавить пользователя в хранилище
     * Вернуть его же
     */

    public User addUser(User user) {
        String stmt = "INSERT INTO users (userId, userLogin) VALUES (" + user.getId() + ", " +
                user.getName() + ')';
        try {
            qex.executeUpdate(stmt);
        } catch (DataBaseException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    /**
     * Обновить информацию о пользователе
     */
    public User updateUser(User user) {
        String stmt = "UPDATE users SET 'userLogin' = " + user.getName() +
                ", 'userPass' = " + user.getPass() + "WHERE 'userId' = " + user.getId();
        try {
            qex.executeUpdate(stmt);
        } catch (DataBaseException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    /**
     *
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    public User getUser(String login, String pass) {
        User user = new User();
        String count = "SELECT COUNT(DISTINCT userId) FROM users where userLogin = " + login;
        try {
            ResultSet res = qex.execute(count);
            if (res.getInt("total") == 0) {
                return null;
            } else {
                String stmt = "SELECT * FROM messages where userLogin = " + login;
                try {
                    ResultSet result = qex.execute(stmt);
                    user.setName(result.getString("userLogin"));
                    user.setPass(result.getString("userPass"));
                    //message.setType(TE);
                    user.setId(result.getLong("userId"));
                    return user;
                } catch (DataBaseException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */
    public User getUserById(Long id) {
        User user = new User();
        String count = "SELECT COUNT(DISTINCT userId) FROM users where userId = " + id;
        try {
            ResultSet res = qex.execute(count);
            if (res.getInt("total") == 0) {
                return null;
            } else {
                String stmt = "SELECT * FROM messages where userId = " + id;
                try {
                    ResultSet result = qex.execute(stmt);
                    user.setName(result.getString("userLogin"));
                    user.setPass(result.getString("userPass"));
                    //message.setType(TE);
                    user.setId(result.getLong("userId"));
                    return user;
                } catch (DataBaseException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
