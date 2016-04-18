package arhangel.dim.core.messages;


public class LoginMessage extends Message {
    private String username;
    private String password;

    public LoginMessage(String username, String password) {
        super();
        this.setType(Type.MSG_LOGIN);
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "{" + super.toString() +
                ", login=" + username +
                ", password=" + password +
                "}";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
