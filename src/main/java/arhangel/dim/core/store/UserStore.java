package arhangel.dim.core.store;

import arhangel.dim.core.User;

/**
 * Хранилище информации о пользователе
 */
public interface UserStore {

    String getUserInformation(Long userId) throws StorageException;

    User getUserByUsername(String username) throws StorageException;

    Long addUser(User user) throws StorageException;

}

