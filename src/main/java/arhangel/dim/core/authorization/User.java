package arhangel.dim.core.authorization;

/**
 * Класс-информация о пользователе
 */
public class User {

    private String name;
    private String password;
    private String nickname;
    private int id;

    public User() {}

    public User(String name, String password, String nickname) {
        this.name = name;
        this.password = password;
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getNick() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
