package arhangel.dim.core.store;

/**
 * Created by valeriyasin on 5/23/16.
 */

public class DbInitializer {
    public static void init() {
        String createUsersTable = "CREATE TABLE Users\n" +
                "(\n" +
                "userId Long,\n" +
                "userLogin varchar(255),\n" +
                "userPass varchar(255),\n" +
                ");";
        String createChatUsersTable = "CREATE TABLE chat_users\n" +
                "(\n" +
                "userId Long,\n" +
                "chatId Long,\n" +
                "adminId Long,\n" +
                ");";

        String createMessagesTable = "CREATE TABLE messages\n" +
                "(\n" +
                "userId Long,\n" +
                "chatId Long,\n" +
                "messageId Long,\n" +
                "Text varchar(255),\n" +
                ");";
    }
}
