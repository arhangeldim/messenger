package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by thefacetakt on 19.04.16.
 */
public class LoginMessage extends Message {
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        LoginMessage message = (LoginMessage) other;
        return Objects.equals(login, message.getLogin()) &&
                Objects.equals(password, message.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login);
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public LoginMessage() {
        setType(Type.MSG_LOGIN);
    }
}
