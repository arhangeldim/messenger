package arhangel.dim.core.store.dao;

import arhangel.dim.core.messages.TextMessage;

import java.util.List;

/**
 * Created by olegchuikin on 22/05/16.
 */
public interface MessageDao extends GenericDao<TextMessage, Long> {

    List<TextMessage> getMessagesWithChatId(Long chatId) throws PersistException;

}
