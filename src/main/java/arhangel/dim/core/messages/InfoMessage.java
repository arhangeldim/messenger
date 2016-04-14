package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by philip on 12.04.16.
 */
public class InfoMessage extends Message {
    private long id;

    public long getMesId() { return this.id;}
    public void setMesId(long id) { this.id = id;}

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
