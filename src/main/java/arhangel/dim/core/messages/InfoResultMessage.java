package arhangel.dim.core.messages;

import arhangel.dim.core.User;

public class InfoResultMessage extends Message {
    private String userInfo;

    public InfoResultMessage(String userInfo) {
        this.setType(Type.MSG_INFO_RESULT);
        this.userInfo = userInfo;
    }

    public String getUser() {
        return userInfo;
    }

    public void setUser(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return userInfo;
    }
}
