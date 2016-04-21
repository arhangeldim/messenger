package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by olegchuikin on 19/04/16.
 */
public interface DaoFactory {

    public Connection getConnection() throws SQLException;

    public UserStore getUserStoreDao() throws SQLException;

    public MessageStore getMessageStoreDao() throws SQLException;
}
