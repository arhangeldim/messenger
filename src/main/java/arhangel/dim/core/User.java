package arhangel.dim.core;

/**
 * Представление пользователя
 */
public class User {
    private Long id;
    private String name;
    private String password;

    public User(){

    }

    public User(String name, String password) {
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
        String result = "User id: " + this.id + ". Name: " + this.name;
        return  result;
    }
}
