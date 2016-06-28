package arhangel.dim.core;

import sun.security.x509.X509CertInfo;

/**
 * Представление пользователя
 */
public class User {
    private Long id;
    private String name;
    private String password;

    public User() {}

    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public User(String name, String password) {
        this.id = null;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "id = " + getId() + "\n" +
                "login = " + getName() + "\n" +
                "pass = " + getPassword();
    }


}
