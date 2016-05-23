package arhangel.dim.core.store.dao;

import java.io.Serializable;

/**
 * Интерфейс идентифицируемых объектов.
 */
public interface Identified<K extends Serializable> {

    /**
     * Возвращает идентификатор объекта
     */
    K getId();

    void setId(K key);
}
