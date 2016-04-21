package arhangel.dim.core.messages;

/**
 * Created by olegchuikin on 18/04/16.
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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        LoginMessage that = (LoginMessage) object;

        if (login != null ? !login.equals(that.login) : that.login != null) {
            return false;
        }
        return !(password != null ? !password.equals(that.password) : that.password != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "id='" + getId() + '\'' +
                "login='" + login + '\'' +
                "password='" + password + '\'' +
                '}';
    }

}
