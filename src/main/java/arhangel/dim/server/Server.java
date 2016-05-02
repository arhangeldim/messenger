package arhangel.dim.server;

import arhangel.dim.core.store.dao.DaoFactory;
import arhangel.dim.session.SessionsManager;

/**
 * Created by olegchuikin on 28/04/16.
 */
public interface Server {

    void start();

    DaoFactory getDbFactory();

    SessionsManager getSessionsManager();

}
