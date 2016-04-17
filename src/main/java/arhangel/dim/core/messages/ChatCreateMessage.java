package arhangel.dim.core.messages;

import arhangel.dim.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Арина on 17.04.2016.
 */
public class ChatCreateMessage extends Message {
    private String[] userList;

    public void setUserList(String[] userList) {this.userList = userList;}

    public String[] getUserList() {return userList;}

}
