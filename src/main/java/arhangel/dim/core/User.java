package arhangel.dim.core;

import arhangel.dim.core.service.AuthorizationService;

import java.math.BigInteger;

/**
 * Представление пользователя
 */
public class User {
    private Long id;
    private String name;
    private String hash;

    public User() {}

    public User(final String name) {
        this.name = name;
    }

    public User(final String name, final String password) {
        this.name = name;
        hash = password;
    }

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

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setPass(final String password) {
        hash = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + name + '\'' +
                ", hash=" + hash +
                ", userID=" + id +
                '}';
    }
}
