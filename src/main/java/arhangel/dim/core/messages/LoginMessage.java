package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by tatiana on 19.04.16.
 */
public class LoginMessage extends Message {
    private String login;
    private String password;

    public LoginMessage() {
        this.setType(Type.MSG_LOGIN);
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LoginMessage loginMessage = (LoginMessage) other;
        return Objects.equals(login, loginMessage.login) &&
                Objects.equals(password, loginMessage.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLogin(), getPassword());
    }

    @Override
    public String toString() {
        return "LoginMessage: " +
                "login: " + getLogin() + '\n' +
                "password: " + getPassword() + '\n';
    }
}
