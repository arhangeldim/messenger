package arhangel.dim.core.session;

import java.util.HashSet;
import java.util.Set;

/**
 * Хранение всех активных сессий
 */
public class SessionManager {
    private Set<Session> sessions;

    public SessionManager() {
        sessions = new HashSet<>();
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    public synchronized void addSession(Session session) {
        sessions.add(session);
    }

    public Session getSessionById(int id) {
        for (Session it: sessions) {
            if (it.getCurrentUserId() == id) {
                return it;
            }
        }
        return null;
    }
}
