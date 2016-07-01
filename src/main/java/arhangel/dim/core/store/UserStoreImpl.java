package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.dbservice.dao.UsersDao;
import arhangel.dim.core.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserStoreImpl implements UserStore {
    private static Logger log = LoggerFactory.getLogger(UserStore.class);

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
            log.error("Невозможно получить данные пользователя:" + login, e);
        }
        return false;
    }

    // Добавить пользователя в хранилище
    @Override
    public User addUser(User user) {


        try {
            usersDao.addUser(user.getName(), user.getHash());
        } catch (Exception ioExc) {
            log.error(ioExc.getMessage());
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
            log.error("Проблема с получением данных пользователя: " + name, e);
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
            log.error("Проблема с получением данных пользователя с id: " + id, e);
        }
        return null;
    }

    @Override
    public User updateUser(User user) {

        try {
            usersDao.setNewPass(user.getName(), user.getHash());
        } catch (Exception e) {
            log.error("Проблема с изменением данных пользователя: " + user.getName(), e);
        }
        return user;
    }

    public void close() {
        usersDao.close();
    }

}
