package arhangel.dim.core.store.dao;

/**
 * Created by olegchuikin on 19/04/16.
 */

/**
 * Фабрика объектов для работы с базой данных
 */
public interface DaoFactory<ContextT> {

    /**
     * Возвращает подключение к базе данных
     */
    ContextT getContext() throws PersistException;

    /**
     * Возвращает объект для управления персистентным состоянием объекта
     */
    GenericDao getDao(Class dtoClass) throws PersistException;
}
