package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.dbservice.dao.UsersDao;
import arhangel.dim.core.service.AuthorizationService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tatiana on 19.04.16.
 */
public class UserStoreImpl implements UserStore {
    private Map<Long, User> users;
    private UsersDao usersDao;
    private Map<String, Long> userLogins = new HashMap<>();

    public UserStoreImpl(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public boolean isUserExist(String login) {
        try {
            return (usersDao.getUser(login) != null);
        } catch (Exception e) {
            System.err.println("can't check existing of user with login=" + login);
            e.printStackTrace();

        }
        return false;
    }

    // Добавить пользователя в хранилище
    @Override
    public User addUser(User user) {


        try {
            //userCounter.incrementAndGet();
            usersDao.addUser(user.getName(), user.getHash());
            //userLogins.put(user.getName(), user.getUserID());
            //users.put(user.getUserID().longValue(), user);
            //user.setUserID(userCounter.longValue());

        } catch (Exception ioExc) {
            System.err.println(ioExc.getMessage());
            return null;
        }
        return user;
    }

    // Получить пользователя по имени и паролю
    @Override
    public User getUser(String name, String pass) {

        User user;

        try {
            user = usersDao.getUser(name);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("some troubles with downloading user with login=" + name);
            return null;
        }

        if (user == null) {
            return null;
        }

        if (AuthorizationService.isCorrect(user, pass)) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {

        try {
            return usersDao.getUserById(id);
        } catch (Exception e) {
            System.err.println("UserStorage:getUserById failed to get user with id=" + id);
        }
        return null;
    }

    @Override
    public User updateUser(User user) {

        try {
            usersDao.setNewPass(user.getName(), user.getHash());
        } catch (Exception e) {
            System.err.println("UserStorage:updateUserPass failed to update pass for user=" + user.getName());
            e.printStackTrace();
        }
        return user;
    }

    public void close() {
        usersDao.close();
    }

}
