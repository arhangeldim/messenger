package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by tatiana on 14.05.16.
 */
public class InfoMessage extends Message {
    public InfoMessage() {
        this.setType(Type.MSG_INFO);
    }

    private String info = "";
    private long userId;
    private boolean isArg;

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setArg(boolean isArg) {
        this.isArg = isArg;
    }

    public boolean isArg() {
        return isArg;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return Objects.equals(info, ((InfoMessage)object).info) &&
                Objects.equals(userId, ((InfoMessage)object).userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, info);
    }

    @Override
    public String toString() {
        return "User id: " + userId + " info: " + info;
    }
}
