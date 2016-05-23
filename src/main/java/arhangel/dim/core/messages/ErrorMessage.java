package arhangel.dim.core.messages;

/**
 * Created by olegchuikin on 23/05/16.
 */
public class ErrorMessage extends Message {
    public ErrorMessage() {
        super(Type.MSG_ERROR);
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

        ErrorMessage that = (ErrorMessage) object;

        return !(text != null ? !text.equals(that.text) : that.text != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
