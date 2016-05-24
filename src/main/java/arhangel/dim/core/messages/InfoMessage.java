package arhangel.dim.core.messages;

public class InfoMessage extends Message {
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "login='" + login + '\'' +
                "} " + super.toString();
    }
}
