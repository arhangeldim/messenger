package arhangel.dim.core;

/**
 * Представление пользователя
 */
public class User {
    private Long id;
    private String login;
    private String secret;

    public User(String login, String secret) {
        setLogin(login);
        setSecret(secret);
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
