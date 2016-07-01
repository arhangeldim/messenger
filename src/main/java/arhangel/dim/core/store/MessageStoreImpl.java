package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.dbservice.dao.UsersDao;
import arhangel.dim.core.messages.Message;

import java.util.List;

public class MessageStoreImpl implements MessageStore {
    UsersDao usersDao;

    public MessageStoreImpl(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public List<Long> getUsersByChatId(Long chatId) {
        List<Long> users = usersDao.getUsersByChatId(chatId);
        return users;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {

        List<Long> chats = usersDao.getChatsByUserId(userId);
        return chats;

    }

    @Override
    public Chat getChatById(Long chatId) {

        Chat chat = new Chat();
        List<Long> userIds = getUsersByChatId(chatId);
        List<Long> messagesIds = getMessagesFromChat(chatId);

        for (Long userId : userIds) {
            chat.addParticipant(userId);
        }

        /*for (Long messageId : messagesIds) {
            chat.addMessage(messageId);
        }*/

        chat.setId(chatId);

        return chat;

    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        List<Long> messageList = usersDao.getMessagesByChatId(chatId);
        return messageList;
    }

    @Override
    public Message getMessageById(Long messageId) {

        return usersDao.getMessageById(messageId);

    }

    @Override
    public void addMessage(Long chatId, Message message) {
        usersDao.addMessage(chatId, message);
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        usersDao.addUserToChat(userId, chatId);
    }

    @Override
    public Chat createChat(Long chatCreator, List<Long> userIds) {
        Chat chat = new Chat();
        chat.setCreatorId(chatCreator);

        usersDao.addChat(chat);

        for (Long id : userIds) {
            chat.addParticipant(id);
            addUserToChat(id, chat.getId());
        }

        return chat;
    }
}
