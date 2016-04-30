package arhangel.dim.session;

import org.jboss.netty.channel.Channel;

import java.util.List;

/**
 * Created by olegchuikin on 29/04/16.
 */
public interface SessionsManager {

    List<Session> getSessionsByUserId(Long userId);

    void addSession(Session session) throws AddSessionToManagerException;

    void removeSession(Session session);

}
