package arhangel.dim.core.messages;


public class LoginMessage extends Message {
    private String name;
    private String password;

    public LoginMessage(String name, String password) {
        this.setType(Type.MSG_LOGIN);
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
