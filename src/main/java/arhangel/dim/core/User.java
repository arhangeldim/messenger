package arhangel.dim.core;

import arhangel.dim.core.store.dao.Identified;

/**
 * Представление пользователя
 */
public class User implements Identified<Long> {
    private Long id;
    private String name;
    private String password;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("User[%d]:%s %s", id, name, password);
    }
}
