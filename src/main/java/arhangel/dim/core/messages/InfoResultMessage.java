package arhangel.dim.core.messages;


public class InfoResultMessage extends Message {

    private String login;
    private String password;
    private String info;

    public InfoResultMessage(long id, String login, String password, String info) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.info = info;
    }

    public long getMesId() {
        return this.id;
    }

    public void setMesId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String login) {
        this.password = password;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
