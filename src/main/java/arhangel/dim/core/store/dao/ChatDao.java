package arhangel.dim.core.store.dao;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;

import java.util.List;

/**
 * Created by olegchuikin on 02/05/16.
 */
public interface ChatDao extends GenericDao<Chat, Long> {

    List<Chat> getChatsByAdmin(User admin) throws PersistException;

}
