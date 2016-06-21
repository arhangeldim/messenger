package arhangel.dim.core.store;

import java.util.List;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId) throws StorageException;

    /**
     * получить информацию о чате
     */
    List<Long> getChatParticipansById(Long chatId) throws StorageException;

    Long addChat(Long adminId, Long... participants) throws StorageException;

    /**
     * Список сообщений из чата
     */
    List<String> getMessagesFromChat(Long chatId) throws StorageException;

    /**
     * Добавить сообщение в чат
     */
    Long addMessage(Long userId, Long chatId, String text) throws StorageException;

}
