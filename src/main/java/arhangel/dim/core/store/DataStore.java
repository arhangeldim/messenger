package arhangel.dim.core.store;

import arhangel.dim.core.commands.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Объединение всех хранилищ
 */
public class DataStore {
    private UserStore userStore;
    private Map<String, Command> commandsStore;
    private ChatStore chatStore;
    private Connection connection;

    public DataStore(UserStore fileUserStore, ChatStore chatStore, Connection c) {
        this.userStore = fileUserStore;
        this.chatStore = chatStore;
        this.connection = c;
        commandsStore = new HashMap<>();
        commandsStore.put("/register", new RegisterCommand());
        commandsStore.put("/login", new LoginCommand());
        commandsStore.put("/info", new UserInfoCommand());
        commandsStore.put("/chat_list", new ChatListCommand());
        commandsStore.put("/chat_create", new ChatCreateCommand());
        commandsStore.put("/chat_history", new ChatHistoryCommand());
        commandsStore.put("/text", new TextCommand());
        commandsStore.put("/help", new HelpCommand());

    }

    public UserStore getUserStore() {
        return userStore;
    }

    public ChatStore getChatStore() {
        return chatStore;
    }

    public Map<String, Command> getCommandsStore() {
        return commandsStore;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws Exception {
    }
}
