package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import org.apache.log4j.Logger;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDao {
    private static Logger log = Logger.getLogger(UserDao.class.getName());
    DaoFactory daoFactory =  DaoFactory.getInstance();

    public TextMessage addMessage(Long chatId, TextMessage textMessage) {

        TextMessage newTextMsg = null;
        try {
            Connection conn = daoFactory.connect();

            log.trace("Creating prepared statement");
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "insert into Messages (user_id, msg_text, chat_id) " +
                    "values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, textMessage.getSenderId());
            preparedStatement.setString(2, textMessage.getText());
            preparedStatement.setLong(3, chatId);

            int affectedRows = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                newTextMsg = new TextMessage();
                newTextMsg.setChatId(generatedKeys.getLong("chat_id"));
                newTextMsg.setSenderId(generatedKeys.getLong("user_id"));
                newTextMsg.setText(generatedKeys.getString("msg_text"));
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return newTextMsg;
    }

    public List<Long> getChatsByUserId(Long userId) {
        return null;
    }

    public List<Long> getUsersByChatId(Long chatId) {
        Connection conn = daoFactory.connect();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "select user_id from user_chat where chat_id = ?");

            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            Long userInChatId = null;
            List<Long> usersInChatList = new ArrayList<>();
            while (resultSet.next()) {
                userInChatId = resultSet.getLong("user_id");
                usersInChatList.add(userInChatId);

            }
            return usersInChatList;

        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * получить информацию о чате
     */
    public Chat getChatById(Long chatId) {
        return null;
    }

    /**
     * Список сообщений из чата
     */
    public List<Long> getMessagesFromChat(Long chatId) {
        return null;
    }

    /**
     * Получить информацию о сообщении
     */
    public Message getMessageById(Long messageId) {
        return null;
    }

    /**
     * Добавить пользователя к чату
     */
    public void addUserToChat(Long userId, Long chatId) {

    }
}
