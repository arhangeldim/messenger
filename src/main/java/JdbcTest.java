import arhangel.dim.core.User;
import arhangel.dim.core.store.PersistException;
import arhangel.dim.core.store.PostgresqlDaoFactory;
import arhangel.dim.core.store.PostgresqlUserStore;
import arhangel.dim.core.store.UserStore;

import java.sql.SQLException;

/**
 * Created by olegchuikin on 08/04/16.
 */
public class JdbcTest {
    public static void main(String[] args) {

        try {
            PostgresqlDaoFactory factory = new PostgresqlDaoFactory();
            UserStore userStoreDao = factory.getUserStoreDao();

            User user = new User();
            user.setName("Hi");
            user.setPassword("pass");

            user = userStoreDao.addUser(user);

            for (User user1 : ((PostgresqlUserStore) userStoreDao).getAll()) {
                System.out.println(user1);
            }
            System.out.println("===================================");

//            User user1 = userStoreDao.getUser("asd", "adf");
//            System.out.println(user1);
            User userById = userStoreDao.getUserById(3L);
            System.out.println(userById);

            User user1 = userStoreDao.getUser("Hi", "pass");

            System.out.println(user1);
            user1.setName("NEW NAME");
            userStoreDao.updateUser(user1);
            Long id = user1.getId();
            user1 = userStoreDao.getUserById(id);
            System.out.println(user1);

//            System.out.println(user.getName());
//            System.out.println(user.getId());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PersistException e) {
            e.printStackTrace();
        }

    }
}
