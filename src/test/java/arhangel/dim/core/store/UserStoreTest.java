package arhangel.dim.core.store;

import org.junit.BeforeClass;
import org.junit.Test;

import arhangel.dim.container.Container;

/**
 *
 */
public class UserStoreTest {

    static UserStore store;

    @BeforeClass
    public static void init() {
        try {
            Container container = new Container("server.xml");
            store = (UserStore) container.getByName("userStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUser() throws Exception {

    }

    @Test
    public void getUser() throws Exception {

    }

    @Test
    public void getUserById() throws Exception {

    }
}