package arhangel.dim.core.store;

import java.util.List;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.TextMessage;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид чатов заданного пользователя
     */
    List<Long> getChatsIdByUserId(Long userId);

    /**
     * получить информацию о чате
     */
    Chat getChatById(Long chatId);

    /**
     * Добавить сообщение в чат
     */
    TextMessage addMessage(TextMessage textMessage);

    /**
     * Создать чат
     */
    Chat addChat(Long adminId, List<Long> userIdList);

    Chat addChat(Long adminId, Long userId);

    List<Long> getUsersIdByChatId(Long chatId);


}
