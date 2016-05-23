package arhangel.dim.session;

import arhangel.dim.server.Server;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by olegchuikin on 29/04/16.
 */
public class FixSizedSessionManager implements SessionsManager {

    static Logger log = LoggerFactory.getLogger(FixSizedSessionManager.class);

    private Server server;
    private final int sessionsLimit;

    private List<Session> sessions;

    public FixSizedSessionManager(Server server, int sessionsLimit) {
        this.server = server;
        this.sessionsLimit = sessionsLimit;

        sessions = new LinkedList<>();
    }

    @Override
    public synchronized List<Session> getSessionsByUserId(Long userId) {
        List<Session> result = new LinkedList<>();
        for (Session session : sessions) {
            if (session.getUser() != null && session.getUser().getId().equals(userId)) {
                result.add(session);
            }
        }
        return result;
    }

    @Override
    public synchronized void addSession(Session session) throws AddSessionToManagerException {
        if (sessions.size() >= sessionsLimit) {
            throw new AddSessionToManagerException("Limit of connections is reached");
        }
        sessions.add(session);
        log.info("Session added to Session Manager");
    }

    @Override
    public synchronized void removeSession(Session session) {
        if (session == null) {
            return;
        }
        if (sessions.contains(session)) {
            log.info("Session deleted from session manager");
            sessions.remove(session);
        }
    }
}
