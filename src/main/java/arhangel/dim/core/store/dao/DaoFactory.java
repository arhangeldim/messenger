package arhangel.dim.core.store.dao;

/**
 * Created by olegchuikin on 19/04/16.
 */

import arhangel.dim.core.User;

/**
 * Фабрика объектов для работы с базой данных
 */
public interface DaoFactory<Context> {

    interface DaoCreator<Context> {
        public GenericDao create(Context context);
    }

    /**
     * Возвращает подключение к базе данных
     */
    Context getContext() throws PersistException;

    /**
     * Возвращает объект для управления персистентным состоянием объекта
     */
    GenericDao getDao(Class dtoClass) throws PersistException;
}
