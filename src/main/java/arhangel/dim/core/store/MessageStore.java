package arhangel.dim.core.store;

import java.util.List;
import java.util.Set;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.TextMessage;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид чатов с заданным пользователем
     */
    Set<Long> getChatsByUserId(Long userId);

    /**
     * получить информацию о чате
     */
    Chat getChatById(Long chatId);

    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId);

    /**
     * Получить информацию о сообщении
     */
    TextMessage getMessageById(Long messageId);

    /**
     * Добавить сообщение в чат
     */
    TextMessage addMessage(TextMessage message);

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId);

    /**
     * Создать чат
     */
    Chat addChat(Chat chat);

    void init() throws Exception;

}
