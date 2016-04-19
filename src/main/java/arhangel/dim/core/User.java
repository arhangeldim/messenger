package arhangel.dim.core;

/**
 * Представление пользователя
 */
public class User {
    private Long id;
    private String login;
    private String password;

    public User(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
