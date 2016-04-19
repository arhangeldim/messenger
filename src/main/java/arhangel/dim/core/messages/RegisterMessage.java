package arhangel.dim.core.messages;

/**
 * Created by Agafu on 19.04.2016.
 */
public class RegisterMessage extends Message {

    private String login;
    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
