package arhangel.dim.core.messages;


public class LoginMessage extends Message {
    private String username;
    private String password;

    public LoginMessage(String username, String password) {
        super();
        this.setType(Type.MSG_LOGIN);
        this.username = username;
        this.password = password;
    }
}
