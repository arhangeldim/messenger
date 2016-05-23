package arhangel.dim.core.store.dao;

import java.sql.*;
import java.util.List;

/**
 * Абстрактный класс предоставляющий базовую реализацию CRUD операций с использованием JDBC.
 *
 * @param <T>  тип объекта персистенции
 * @param <PK> тип первичного ключа
 */
public abstract class AbstractJDBCDao<T extends Identified<PK>, PK extends Long> implements GenericDao<T, PK> {

    protected String TABLE_NAME;

    protected Connection connection;

    /**
     * Возвращает sql запрос для получения всех записей.
     * <p>
     * SELECT * FROM [Table]
     */
    public abstract String getSelectQuery();

    /**
     * Возвращает sql запрос для вставки новой записи в базу данных.
     * <p>
     * INSERT INTO [Table] ([column, column, ...]) VALUES (?, ?, ...);
     */
    public abstract String getCreateQuery();

    /**
     * Возвращает sql запрос для обновления записи.
     * <p>
     * UPDATE [Table] SET [column = ?, column = ?, ...] WHERE id = ?;
     */
    public abstract String getUpdateQuery();

    /**
     * Возвращает sql запрос для удаления записи из базы данных.
     * <p>
     * DELETE FROM [Table] WHERE id= ?;
     */
    public abstract String getDeleteQuery();

    /**
     * Разбирает ResultSet и возвращает список объектов соответствующих содержимому ResultSet.
     */
    protected abstract List<T> parseResultSet(ResultSet rs) throws PersistException;

    /**
     * Устанавливает аргументы insert запроса в соответствии со значением полей объекта object.
     */
    protected abstract void prepareStatementForInsert(PreparedStatement statement, T object) throws PersistException;

    /**
     * Устанавливает аргументы update запроса в соответствии со значением полей объекта object.
     */
    protected abstract void prepareStatementForUpdate(PreparedStatement statement, T object) throws PersistException;

    @Override
    public T getByPK(Long key) throws PersistException {
        List<T> list;
        String sql = getSelectQuery();
        sql += " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, key);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new PersistException("Received more than one record.");
        }
        return list.iterator().next();
    }

    protected List<T> getByStringFieldValue(String column, String value) throws PersistException {
        List<T> list;
        String sql = getSelectQuery();
        sql += String.format(" WHERE %s = ? ", column);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return list;
    }

    protected List<T> getByLongFieldValue(String column, Long value) throws PersistException {
        List<T> list;
        String sql = getSelectQuery();
        sql += String.format(" WHERE %s = ? ", column);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, value);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override
    public List<T> getAll() throws PersistException {
        List<T> list;
        String sql = getSelectQuery();
//        sql += ";";
        System.out.println(sql);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return list;
    }

    @Override
    public T persist(T object) throws PersistException {
        if (object.getId() != null) {
            throw new PersistException("Object is already persist.");
        }
        // Добавляем запись
        String sql = getCreateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatementForInsert(statement, object);

            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("On persist modify more then 1 record: " + count);
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long pk = generatedKeys.getLong(1);
                    object.setId((PK) pk);
                    return object;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public void update(T object) throws PersistException {
        String sql = getUpdateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            prepareStatementForUpdate(statement, object); // заполнение аргументов запроса оставим на совесть потомков
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("On update modify more then 1 record: " + count);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    public void delete(T object) throws PersistException {
        String sql = getDeleteQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try {
                statement.setObject(1, object.getId());
            } catch (Exception e) {
                throw new PersistException(e);
            }
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("On delete modify more then 1 record: " + count);
            }
            statement.close();
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    public AbstractJDBCDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void clearTables() throws PersistException {
        String sql;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            throw new PersistException(e);
        }
    }
}
