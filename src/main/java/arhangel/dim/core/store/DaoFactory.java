package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by olegchuikin on 19/04/16.
 */
public interface DaoFactory {

    Connection getConnection() throws SQLException;

    UserStore getUserStoreDao() throws SQLException;

    MessageStore getMessageStoreDao() throws SQLException;
}
