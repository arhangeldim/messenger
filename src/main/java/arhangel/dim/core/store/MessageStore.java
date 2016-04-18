package arhangel.dim.core.store;

import arhangel.dim.core.messages.TextMessage;

import java.util.List;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    Long addChat(List<Long> participants) throws StorageException;

    List<TextMessage> getMessagesByChatId(Long chatId) throws StorageException;

    List<Long> getChatsByUserId(Long userId) throws StorageException;

    Long addTextMessage(Long chatId, TextMessage message) throws StorageException;

}
