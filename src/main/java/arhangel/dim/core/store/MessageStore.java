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
    Chat getChatById(Long chatId) throws StorageException;

    /**
     * Список сообщений из чата
     */
    List<TextMessage> getMessagesFromChat(Long chatId) throws StorageException;

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId) throws StorageException;

    /**
     * Добавить сообщение в чат
     */
    Long addMessage(Long chatId, TextMessage message) throws StorageException;

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId) throws StorageException;
    /**
     * Добавить чат
     */
    Long addChat(List<Long> participants) throws StorageException;

}
