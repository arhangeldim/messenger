package arhangel.dim.core.store.dao;

import arhangel.dim.core.User;

/**
 * Created by olegchuikin on 02/05/16.
 */
public interface UserDao extends GenericDao<User, Long> {

    User getUserByLogin(String login) throws PersistException;

}
