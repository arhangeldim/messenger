package arhangel.dim.core.messages;

import arhangel.dim.core.messages.Message;

/**
 * Created by Арина on 17.04.2016.
 */
public class LoginMessage extends Message {
    private String login;
    private String password;

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
