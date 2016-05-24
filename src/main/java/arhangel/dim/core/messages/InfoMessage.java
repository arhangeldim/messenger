package arhangel.dim.core.messages;

/**
 * Created by olegchuikin on 02/05/16.
 */
public class InfoMessage extends Message {

    private Long target;

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        InfoMessage that = (InfoMessage) object;

        return !(target != null ? !target.equals(that.target) : that.target != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "id='" + getId() + '\'' +
                "target='" + target + '\'' +
                '}';
    }
}
