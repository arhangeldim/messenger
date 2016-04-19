package arhangel.dim.core.messages;

public class UserCreateMessage extends Message {
    private String username;
    private String password;

    public UserCreateMessage() {
        super();
        this.setType(Type.MSG_USER_CREATE);
    }

    public String getUsername() {
        return username;
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

    @Override
    public String toString() {
        return "{" + super.toString() +
                ", login=" + username +
                ", password=" + password +
                "}";
    }
}
