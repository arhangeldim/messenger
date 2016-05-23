package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.store.dao.DaoFactory;
import arhangel.dim.core.store.dao.GenericDao;
import arhangel.dim.core.store.dao.PersistException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class PostgresDaoFactory implements DaoFactory<Connection> {

    private Connection connection;

    private Map<Class, GenericDao> daos;

    private String dbUrl;
    private String dbLogin;
    private String dbPassword;

    public PostgresDaoFactory(String dbUrl, String dbLogin, String dbPassword) throws PersistException {
        this.dbUrl = dbUrl;
        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
        try {
            Class.forName("org.postgresql.Driver");

            daos = new HashMap<>();
            daos.put(User.class, new PostgresUserDao(getContext()));
            daos.put(TextMessage.class, new PostgresMessagesDao(getContext()));
            daos.put(Chat.class, new PostgresChatsDao(getContext(), this));
        } catch (ClassNotFoundException e) {
            throw new PersistException(e);
        }
    }

    @Override
    public Connection getContext() throws PersistException {
        if (connection == null) {
            try {
//                connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/ochuikin", "trackuser", "trackuser");
                connection = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
            } catch (SQLException e) {
                throw new PersistException(e);
            }
        }
        return connection;
    }

    @Override
    public GenericDao getDao(Class dtoClass) throws PersistException {
        return daos.get(dtoClass);
    }
}
