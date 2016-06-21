package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.util.List;

/**
 * Хранилище информации о пользователе
 */
public interface UserStore {

    /**
     * Добавить пользователя в хранилище
     * Вернуть его же
     */
    User addUser(User user) throws StorageException;

    List<Long> getChatListByUser(User user) throws StorageException;

    /**
     *
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    User getUser(String login) throws StorageException;

    /**
     *
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */
    User getUserById(Long id) throws StorageException;
}
