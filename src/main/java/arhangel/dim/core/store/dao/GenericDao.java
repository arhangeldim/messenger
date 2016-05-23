package arhangel.dim.core.store.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Унифицированный интерфейс управления персистентным состоянием объектов
 *
 * @param <T>  тип объекта персистенции
 * @param <K> тип первичного ключа
 */
public interface GenericDao<T extends Identified<K>, K extends Serializable> {

    /**
     * Создает новую запись и соответствующий ей объект
     */
    T create() throws PersistException;

    /**
     * Создает новую запись, соответствующую объекту object
     */
    T persist(T object) throws PersistException;

    /**
     * Возвращает объект соответствующий записи с первичным ключом key или null
     */
    T getByPk(K key) throws PersistException;

    /**
     * Сохраняет состояние объекта group в базе данных
     */
    void update(T object) throws PersistException;

    /**
     * Удаляет запись об объекте из базы данных
     */
    void delete(T object) throws PersistException;

    /**
     * Возвращает список объектов соответствующих всем записям в базе данных
     */
    List<T> getAll() throws PersistException;

    void clearTables() throws PersistException;
}
