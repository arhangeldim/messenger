package arhangel.dim.core.store;


import arhangel.dim.core.commands.Command;
import arhangel.dim.core.commands.LoginCommand;
import arhangel.dim.core.commands.RegisterCommand;
import arhangel.dim.core.commands.UserInfoCommand;
import arhangel.dim.core.commands.TextCommand;
import arhangel.dim.core.commands.ChatCreateCommand;
import arhangel.dim.core.commands.ChatHistoryCommand;
import arhangel.dim.core.commands.ChatListCommand;
import arhangel.dim.core.commands.HelpCommand;

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

    public DataStore(UserStore fileUserStore, ChatStore chatStore, Connection conn) {
        this.userStore = fileUserStore;
        this.chatStore = chatStore;
        this.connection = conn;
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
