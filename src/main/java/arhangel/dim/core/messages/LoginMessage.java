package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by philip on 12.04.16.
 */
public class LoginMessage extends Message {
    private String login;
    private String password;

    public String getLogin() { return this.login;}
    public String getPassword() { return this.password;}

    public void setLogin(String login) { this.login = login;}
    public void setPassword(String password) { this.password = password;}

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLogin(), getPassword());
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "login='" + getLogin() + '\'' +
                "password='" + getPassword() + '\'' +
                '}';
    }

}
