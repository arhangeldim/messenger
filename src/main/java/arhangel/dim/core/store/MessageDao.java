package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.TextMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDao implements MessageStore {
    private DaoFactory daoFactory =  DaoFactory.getInstance();
    private  Connection connection = daoFactory.connect();

    @Override
    public TextMessage addMessage(TextMessage textMessage) {
        TextMessage newTextMsg = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into Messages (user_id, text, chat_id) values (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, textMessage.getSenderId());
            preparedStatement.setString(2, textMessage.getText());
            preparedStatement.setLong(3, textMessage.getChatId());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                newTextMsg = new TextMessage();
                newTextMsg.setId(generatedKeys.getLong(1));
                newTextMsg.setChatId(generatedKeys.getLong("chat_id"));
                newTextMsg.setSenderId(generatedKeys.getLong("user_id"));
                newTextMsg.setText(generatedKeys.getString("msg_text"));
                return newTextMsg;
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public List<Long> getChatsIdByUserId(Long userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "select chat_id from user_groups where user_id = ?")) {
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            Long chatOfUserId = null;
            List<Long> chatsOfUserList = new ArrayList<>();
            while (resultSet.next()) {
                chatOfUserId = resultSet.getLong("chat_id");
                chatsOfUserList.add(chatOfUserId);
            }
            return chatsOfUserList;

        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public List<Long> getUsersIdByChatId(Long chatId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "select user_id from user_groups where chat_id = ?")) {
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

    @Override
    public Chat addChat(Long adminId, Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        return addChat(adminId, userIds);
    }

    @Override
    public Chat addChat(Long adminId, List<Long> userIdList) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into chats (admin_id) values (?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, adminId);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            Long createdChatId = 0L;
            if (generatedKeys.next()) {
                createdChatId = generatedKeys.getLong(1);
            }

            if (createdChatId > 0L) {
                preparedStatement = connection.prepareStatement(
                        "insert into user_groups (user_id, chat_id) values(?, ?)");
                preparedStatement.setLong(2, createdChatId);

                preparedStatement.setLong(1, adminId);
                preparedStatement.executeUpdate();

                for (Long userId : userIdList) {
                    preparedStatement.setLong(1, userId);
                    preparedStatement.executeUpdate();
                }

                Chat newChat = new Chat(createdChatId);
                return newChat;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * получить информацию о чате
     */
    @Override
    public Chat getChatById(Long chatId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM CHATS WHERE chat_id = ?");
            Long adminId;
            List<TextMessage> messages = new ArrayList<>();
            List<Long> participantsId = new ArrayList<>();

            preparedStatement.setLong(1, chatId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                adminId = rs.getLong("admin_id");
            } else {
                return null;
            }

            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM messages WHERE chat_id = ?");
            preparedStatement.setLong(1, chatId);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                messages.add(new TextMessage(
                        rs.getLong("msg_id"),
                        rs.getLong("user_id"),
                        chatId,
                        rs.getString("text")
                ));
            }

            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM user_groups WHERE chat_id = ?");
            preparedStatement.setLong(1, chatId);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                participantsId.add(rs.getLong("user_id"));
            }

            return new Chat(chatId, adminId, messages, participantsId);
        } catch (SQLException e) {
            e.getMessage();
        }
        return  null;
    }
}