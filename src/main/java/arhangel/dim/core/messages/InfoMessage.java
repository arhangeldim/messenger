package arhangel.dim.core.messages;

import java.util.Objects;


public class InfoMessage extends Message {
    private long usrId;

    public long getUsrId() {
        return this.usrId;
    }

    public void setUsrId(long usrId) {
        this.usrId = usrId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "id='" + getId() + '\'' +
                '}';
    }
}
