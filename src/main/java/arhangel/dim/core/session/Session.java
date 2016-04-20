package arhangel.dim.core.session;

import arhangel.dim.core.authorization.Authorize;
import arhangel.dim.core.store.DataStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Хранение данных о текущей сессии с клиентом
 */
public class Session {
    /**
     * Потоки для общения с клиентом
     */
    private DataInputStream reader;
    private DataOutputStream writer;

    /**
     * Сервис авторизации и регистрации клиентов
     */
    private Authorize authorize;

    private DataStore dataStore;
    private String currentUserName;
    private int currentUserId;

    private SessionManager sessionManager;

    public Session(DataInputStream reader, DataOutputStream writer,
                   Authorize authService, DataStore dataStore, SessionManager manager) {
        this.reader = reader;
        this.writer = writer;
        this.authorize = authService;
        this.dataStore = dataStore;
        this.sessionManager = manager;
        this.currentUserId = -1;
    }

    public DataInputStream getReader() {
        return reader;
    }

    public DataOutputStream getWriter() {
        return writer;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}
