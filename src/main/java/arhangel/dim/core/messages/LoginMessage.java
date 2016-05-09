package arhangel.dim.core.messages;

/**
 * Created by dmitriy on 08.05.16.
 */
public class LoginMessage extends Message {
    private String login;
    private String password;

    public LoginMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
